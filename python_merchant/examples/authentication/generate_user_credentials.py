# -*- coding: utf-8 -*-
# Copyright 2024 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This example will create credentials to use the Merchant API.

This file authenticates either via a provided service-account.json file, a
stored OAuth2 refresh token, or creates credentials and stores a refresh token
via a provided client-secrets.json file.

This example works with web OAuth client ID types.

https://console.cloud.google.com

IMPORTANT: For web app clients types, you must add "http://127.0.0.1" to the
"Authorized redirect URIs" list in your Google Cloud Console project before
running this example.
"""

import hashlib
import os
import re
import socket
import sys
import urllib.parse

from examples.authentication import configuration
from examples.authentication import token_storage
from google.oauth2 import service_account

# If using Web flow, the redirect URL must match exactly what’s configured in
# GCP for the OAuth client.
from google_auth_oauthlib.flow import Flow


_SCOPE = "https://www.googleapis.com/auth/content"
_SERVER = "127.0.0.1"
_PORT = 8080

_REDIRECT_URI = f"http://{_SERVER}:{_PORT}"


def main():
  """Generates OAuth2 credentials."""
  # Gets the configuration object that has the paths on the local machine to
  # the `service-account.json`, `token.json`, and `client-secrets.json` files.
  config = configuration.Configuration().get_config()
  service_account_path = config["service_account_path"]
  print("Attempting to use service account credentials from "
        f"{service_account_path}.")
  if os.path.isfile(service_account_path):
    print("Service account credentials found. Attempting to authenticate.")
    credentials = service_account.Credentials.from_service_account_file(
        service_account_path,
        scopes=[_SCOPE])
    return credentials
  else:
    print("Service account credentials not found.")
    full_token_path = os.path.join(os.getcwd(), config["token_path"])
    print(f"Attempting to use stored token data from {full_token_path}")
    if os.path.isfile(config["token_path"]):
      print("Token file found.")
      print("Attempting to use token file to authenticate")
      return get_credentials_from_token(config)
    else:
      print("Token file not found.")
      client_secrets_path = config["client_secrets_path"]
      print(f"Attempting to use client secrets from {client_secrets_path}.")
      if os.path.isfile(client_secrets_path):
        print("Client secrets file found.")
        print("Attempting to use client secrets to authenticate")
        return get_credentials_from_client_secrets(config)
      else:
        print("Service account file, token file, and client secrets "
              "file do not exist. Please follow the instructions in "
              "the top level ReadMe to create a service account or "
              "client secrets file.")
        exit(1)


def get_credentials_from_token(config):
  """Generates OAuth2 refresh token from stored local token file."""
  credentials = token_storage.Storage(config, _SCOPE).get()
  return credentials


def get_credentials_from_client_secrets(config):
  """Generates OAuth2 refresh token using the Web application flow.

  To retrieve the necessary client_secrets JSON file, first
  generate OAuth 2.0 credentials of type Web application in the
  Google Cloud Console (https://console.cloud.google.com).
  Make sure "http://_SERVER:_PORT" is included the list of
  "Authorized redirect URIs" for this client ID."

  Starts a basic server and initializes an auth request.

  Args:
    config: an instance of the Configuration object.

  Returns:
    Credentials used to authenticate with the Merchant API.
  """
  # A list of API scopes to include in the auth request, see:
  # https://developers.google.com/identity/protocols/oauth2/scopes
  scopes = [_SCOPE]

  # A path to where the client secrets JSON file is located
  # on the machine running this example.
  client_secrets_path = config["client_secrets_path"]

  flow = Flow.from_client_secrets_file(client_secrets_path, scopes=scopes)
  flow.redirect_uri = _REDIRECT_URI

  # Create an anti-forgery state token as described here:
  # https://developers.google.com/identity/protocols/OpenIDConnect#createxsrftoken
  passthrough_val = hashlib.sha256(os.urandom(1024)).hexdigest()

  authorization_url, state = flow.authorization_url(
      access_type="offline",
      state=passthrough_val,
      prompt="consent",
      include_granted_scopes="true",
  )

  print(f"Your state token is: {state}\n")

  # Prints the authorization URL so you can paste into your browser. In a
  # typical web application you would redirect the user to this URL, and they
  # would be redirected back to "redirect_url" provided earlier after
  # granting permission.
  print("Paste this URL into your browser: ")
  print(authorization_url)
  print(f"\nWaiting for authorization and callback to: {_REDIRECT_URI}")

  # Retrieves an authorization code by opening a socket to receive the
  # redirect request and parsing the query parameters set in the URL.
  code = urllib.parse.unquote(get_authorization_code(passthrough_val))

  # Passes the code back into the OAuth module to get a refresh token.
  flow.fetch_token(code=code)
  refresh_token = flow.credentials.refresh_token

  print(f"\nYour refresh token is: {refresh_token}\n")

  # Stores the provided credentials into the appropriate file.
  storage = token_storage.Storage(config, scopes)
  storage.put(flow.credentials)

  return flow.credentials


def get_authorization_code(passthrough_val):
  """Opens a socket to handle a single HTTP request containing auth tokens.

  Args:
    passthrough_val: an anti-forgery token used to verify the request
      received by the socket.

  Returns:
    a str access token from the Google Auth service.
  """
  # Opens a socket at _SERVER:_PORT and listen for a request.
  sock = socket.socket()
  sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
  sock.bind((_SERVER, _PORT))
  sock.listen(1)
  connection, address = sock.accept()
  print(f"Socket address: {address}")
  data = connection.recv(1024)
  # Parses the raw request to retrieve the URL query parameters.
  params = parse_raw_query_params(data)

  try:
    if not params.get("code"):
      # If no code is present in the query params then there will be an
      # error message with more details.
      error = params.get("error")
      message = f"Failed to retrieve authorization code. Error: {error}"
      raise ValueError(message)
    elif params.get("state") != passthrough_val:
      message = "State token does not match the expected state."
      raise ValueError(message)
    else:
      message = "Authorization code was successfully retrieved."
  except ValueError as error:
    print(error)
    sys.exit(1)
  finally:
    response = (
        "HTTP/1.1 200 OK\n"
        "Content-Type: text/html\n\n"
        f"<b>{message}</b>"
        "<p>Please check the console output.</p>\n"
    )

    connection.sendall(response.encode())
    connection.close()

  return params.get("code")


def parse_raw_query_params(data):
  """Parses a raw HTTP request to extract its query params as a dict.

  Note that this logic is likely irrelevant if you're building OAuth logic
  into a complete web application, where response parsing is handled by a
  framework.

  Args:
    data: raw request data as bytes.

  Returns:
    a dict of query parameter key value pairs.
  """
  # Decodes the request into a utf-8 encoded string.
  decoded = data.decode("utf-8")
  # Uses a regular expression to extract the URL query parameters string.
  params = re.search(r"GET\s\/\?(.*) ", decoded).group(1)

  # Splits the parameters to isolate the key/value pairs.
  pairs = [pair.split("=") for pair in params.split("&")]
  # Converts pairs to a dict to make it easy to access the values.
  return {key: val for key, val in pairs}
