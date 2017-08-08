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
"""Backing store implementation for OAuth2 clients based on config data."""

from __future__ import print_function
import json
import os
import sys

from shopping.content import _constants
import google.auth
import google.oauth2


class Storage(object):
  """Simple store for refresh token-based clients."""

  def __init__(self, config):
    self._config = config

  def _token_path(self):
    return os.path.join(self._config['path'], _constants.TOKEN_FILE)

  def get(self):
    """Attempt to retrieve the currently stored token.

    Returns:
      An instance of google.oauth2.credentials.Credentials if token
      retrieval succeeds, or None if it fails for any reason.
    """
    try:
      with open(self._token_path(), 'r') as infile:
        token = json.load(infile)
      client_info = retrieve_client_config(self._config)['installed']
      credentials = google.oauth2.credentials.Credentials(
          None,
          client_id=client_info['client_id'],
          client_secret=client_info['client_secret'],
          refresh_token=token['refresh_token'],
          token_uri=client_info['token_uri'],
          scopes=[_constants.CONTENT_API_SCOPE])
      # Access tokens aren't stored (and may be expired even if we did), so
      # we'll need to refresh to ensure we have valid credentials.
      try:
        credentials.refresh(google.auth.transport.requests.Request())
        print('Using stored credentials from %s.' % self._token_path())
        return credentials
      except google.auth.exceptions.RefreshError:
        print('The stored credentials in the file %s cannot be refreshed, '
              're-requesting access.' % self._token_path())
        return None
    except (IOError, ValueError, KeyError):
      return None

  def put(self, credentials):
    """Store the provided credentials into the appropriate file.

    Args:
      credentials: an instance of google.oauth2.credentials.Credentials.
    """
    token = {
        'refresh_token': credentials.refresh_token,
    }
    with open(self._token_path(), 'w') as outfile:
      json.dump(token, outfile, sort_keys=True, indent=2,
                separators=(',', ': '))


def retrieve_client_config(config):
  client_secrets_path = os.path.join(
      config['path'], _constants.CLIENT_SECRETS_FILE)
  with open(client_secrets_path, 'r') as json_file:
    client_config_json = json.load(json_file)
  if 'installed' not in client_config_json:
    print('Please read the note about OAuth2 client IDs in the '
          'top-level README.')
    sys.exit(1)
  return client_config_json
