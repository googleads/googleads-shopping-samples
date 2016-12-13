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

"""Various constants used in the Content API for Shopping samples."""
import os

# Constants for configuration
CONFIG_DIR = os.path.expanduser('~/.shopping-content-samples')
CONFIG_FILE = os.path.join(CONFIG_DIR, 'merchant-info.json')

# Constants for authentication
CLIENT_SECRETS_FILE = os.path.join(CONFIG_DIR, 'content-oauth2.json')
SERVICE_ACCOUNT_FILE = os.path.join(CONFIG_DIR, 'content-service.json')

# Constants needed for the Content API
SERVICE_NAME = 'content'
SERVICE_VERSION = 'v2'
SANDBOX_SERVICE_VERSION = 'v2sandbox'
API_SCOPE = 'https://www.googleapis.com/auth/' + SERVICE_NAME

# These constants define the identifiers for all of our example products/feeds.
#
# The products will be sold online.
CHANNEL = 'online'
# The product details are provided in English.
CONTENT_LANGUAGE = 'en'
# The products are sold in the USA.
TARGET_COUNTRY = 'US'
