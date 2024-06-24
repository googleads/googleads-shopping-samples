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
"""A module to interact with authentication configuration data."""

import os


class Configuration(object):
  """Simple config object for authentication details."""

  def get_config(self):
    """Returns config object to be used for authentication purposes."""
    config_path = os.path.join(os.path.expanduser("~"), "shopping-samples")
    config_dir = os.path.join(config_path, "content")
    service_account_path = os.path.join(config_dir, "service-account.json")
    client_secrets_path = os.path.join(config_dir, "client-secrets.json")
    # Token_path assumes you call your requests from the top level python
    # directory.
    token_path = "token.json"
    config_object = {
        "service_account_path": service_account_path,
        "client_secrets_path": client_secrets_path,
        "token_path": token_path,
    }

    return config_object

