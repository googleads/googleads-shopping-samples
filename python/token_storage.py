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
import copy
from datetime import datetime
import json
import os
import threading

import _constants
import oauth2client


class Storage(oauth2client.client.Storage):
  """Backing store for refresh token-based clients.

  Unlike the normal file-baked store, this one stores the needed information
  (client_id, access_token, refresh_token) in the Content API sample
  configuration.
  """

  def __init__(self, config):
    super(Storage, self).__init__(lock=threading.Lock())
    self._config = config

  def dump_json(self):
    output_file = os.path.join(self._config['path'], _constants.CONFIG_FILE)
    # Calculated configuration contents should not be written back to the file.
    to_strip = ['path', 'isMCA']
    to_dump = copy.deepcopy(self._config)
    for key in to_strip:
      to_dump.pop(key)
    with open(output_file, 'w') as outfile:
      json.dump(
          to_dump, outfile, sort_keys=True, indent=2, separators=(',', ': '))

  def locked_delete(self):
    del self._config['token']
    self.dump_json()

  def locked_get(self):
    credentials = None
    try:
      _, client_info = oauth2client.clientsecrets.loadfile(
          os.path.join(self._config['path'], _constants.CLIENT_SECRETS_FILE))
      if client_info['client_id'] == self._config['token']['client_id']:
        credentials = oauth2client.client.OAuth2Credentials(
            self._config['token']['access_token'],
            client_info['client_id'],
            client_info['client_secret'],
            self._config['token']['refresh_token'],
            datetime.utcnow(),
            oauth2client.GOOGLE_AUTH_URI,
            'Google Content API for Shopping Samples',
            id_token=self._config['emailAddress'],
            scopes=_constants.API_SCOPE)
        credentials.set_store(self)
    except ValueError:
      pass
    except KeyError:
      pass
    return credentials

  def locked_put(self, credentials):
    # Also set the emailAddress based off of the credentials.
    self._config['emailAddress'] = credentials.id_token
    self._config['token'] = {
        'client_id': credentials.client_id,
        'access_token': credentials.access_token,
        'refresh_token': credentials.refresh_token,
        'scope': [_constants.API_SCOPE],
        'expiration_time_millis': 0,
    }
    self.dump_json()
