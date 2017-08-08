#!/usr/bin/python
#
# Copyright 2016 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Authentication-related info for the Content API for Shopping samples."""

from __future__ import print_function
import os
import sys

from google_auth_oauthlib import flow
from shopping.content import _constants
from shopping.content import token_storage
import google.auth
from google.oauth2 import service_account


def authorize(config):
  """Authorization for the Content API Samples.

  This function uses common idioms found across all the included
  samples, i.e., that service account credentials are located in a
  file called 'client-service.json' and that OAuth2 client credentials
  are located in a file called 'client-oauth2.json', both in the same
  directory as the sample configuration file.  Only one of these files
  needs to exist for authentication, and the service account will be
  chosen first if both exist.

  Args:
      config: dictionary, Python representation of config JSON.

  Returns:
      An google.auth.credentials.Credentials object suitable for
      accessing the Content API.
  """
  try:
    credentials, _ = google.auth.default(scopes=[_constants.CONTENT_API_SCOPE])
    print('Using application default credentials.')
    return credentials
  except google.auth.exceptions.DefaultCredentialsError:
    pass  # Can safely ignore this error, since it just means none were found.
  if 'path' not in config:
    print('Must use Application Default Credentials with no configuration.')
    sys.exit(1)
  service_account_path = os.path.join(config['path'],
                                      _constants.SERVICE_ACCOUNT_FILE)
  client_secrets_path = os.path.join(config['path'],
                                     _constants.CLIENT_SECRETS_FILE)
  if os.path.isfile(service_account_path):
    print('Using service account credentials from %s.' % service_account_path)
    return service_account.Credentials.from_service_account_file(
        service_account_path,
        scopes=[_constants.CONTENT_API_SCOPE])
  elif os.path.isfile(client_secrets_path):
    print('Using OAuth2 client secrets from %s.' % client_secrets_path)
    storage = token_storage.Storage(config)
    credentials = storage.get()
    if credentials and credentials.valid:
      return credentials
    client_config = token_storage.retrieve_client_config(config)
    auth_flow = flow.InstalledAppFlow.from_client_config(
        client_config, scopes=[_constants.CONTENT_API_SCOPE])
    credentials = auth_flow.run_local_server(authorization_prompt_message='')
    storage.put(credentials)
    return credentials
  print('No OAuth2 authentication files found. Checked:', file=sys.stderr)
  print('- Google Application Default Credentials', file=sys.stderr)
  print('- %s' % service_account_path, file=sys.stderr)
  print('- %s' % client_secrets_path, file=sys.stderr)
  print('Please read the accompanying documentation.', file=sys.stderr)
  sys.exit(1)
  return None
