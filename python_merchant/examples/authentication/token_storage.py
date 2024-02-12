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
"""Backing store implementation for OAuth2 clients based on config data."""

from __future__ import print_function
import json
import os
import sys

import google.auth
import google.oauth2


class Storage(object):
  """Simple store for refresh token-based clients."""

  def __init__(self, config, scopes):
    self._config = config
    self._scopes = scopes

  def get(self):
    """Attempts to retrieve the currently stored token.

    Returns:
      An instance of google.oauth2.credentials.Credentials if token
      retrieval succeeds, or None if it fails for any reason.
    """
    try:
      with open(self._config["token_path"], "r") as infile:
        token = json.load(infile)

      client_info = self.retrieve_client_config()["web"]

      credentials = google.oauth2.credentials.Credentials(
          None,
          client_id=client_info["client_id"],
          client_secret=client_info["client_secret"],
          refresh_token=token["refresh_token"],
          token_uri=client_info["token_uri"],
          scopes=self._scopes)

      full_token_path = os.path.join(os.getcwd(), self._config["token_path"])
      # Access tokens aren't stored (and may be expired even if we did), so
      # we'll need to refresh to ensure we have valid credentials.
      try:
        credentials.refresh(google.auth.transport.requests.Request())
        print(f"Using stored credentials from {full_token_path}.")
        return credentials
      except google.auth.exceptions.RefreshError:
        print(f"The stored credentials in the file {full_token_path} cannot "
              "be refreshed ,please delete `token.json` and retry.")
        return None
    except (IOError, ValueError, KeyError):
      return None

  def put(self, credentials):
    """Stores the provided credentials into the appropriate file.

    Args:
      credentials: an instance of google.oauth2.credentials.Credentials.
    """
    print("Attempting to store token")
    token = {"refresh_token": credentials.refresh_token}
    with open(self._config["token_path"], "w") as outfile:
      json.dump(token, outfile, sort_keys=True, indent=2,
                separators=(",", ": "))

    print("Token stored sucessfully")

  def retrieve_client_config(self):
    """Attempts to retrieve the client secret data.

    Returns:
      The client secret data in JSON form if the retrieval succeeds,
      or exits with an error if retrieval fails.
    """
    try:
      with open(self._config["client_secrets_path"], "r") as json_file:
        client_config_json = json.load(json_file)
      if "web" not in client_config_json:
        print("Please read the note about OAuth2 client IDs in the "
              "top-level README.")
        sys.exit(1)

      return client_config_json
    except FileNotFoundError:
      print("Please read the note about OAuth2 client IDs in the "
            "top-level README.")
      sys.exit(1)
