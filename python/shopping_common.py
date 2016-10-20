#!/usr/bin/python
#
# Copyright 2014 Google Inc. All Rights Reserved.
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

"""Common utils for the Content API for Shopping samples."""
from __future__ import print_function

import argparse
import os
import sys
import time

from googleapiclient import discovery
import httplib2
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage
from oauth2client.service_account import ServiceAccountCredentials

SERVICE_NAME = 'content'
SERVICE_VERSION = 'v2'
API_SCOPE = 'https://www.googleapis.com/auth/' + SERVICE_NAME

CLIENT_SECRETS_FILE = 'content-oauth2.json'
SERVICE_ACCOUNT_FILE = 'content-service.json'

# Declare common command-line flags.  All modules must have a merchant ID.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'merchant_id',
    help='The ID of the merchant center.')


# Authenticate and return the Content API service along with any command-line
# flags/arguments.
def init(argv, doc, filename, parents=None):
  """A common initialization routine for the Content API samples.

  This function uses common idioms found across all the included
  samples, i.e., that service account credentials are located in a
  file called 'client-service.json' and that OAuth2 client credentials
  are located in a file called 'client-oauth2.json', both in the same
  directory as the application main file.  Only one of these files needs
  to exist for authentication, and the service account will be chosen
  first if both exist.

  Args:
    argv: list of string, the command-line parameters of the application.
    doc: string, description of the application. Usually set to __doc__.
    filename: string, filename of the application. Usually set to __file__.
    parents: list of argparse.ArgumentParser, additional command-line flags.

  Returns:
    A tuple of (service, flags), where service is the service object and flags
    is the parsed command-line flags.
  """
  service = None
  flags = None
  parent_parsers = [tools.argparser, argparser]
  if parents is not None:
    parent_parsers.extend(parents)

  parser = argparse.ArgumentParser(
      description=doc,
      formatter_class=argparse.RawDescriptionHelpFormatter,
      parents=parent_parsers)
  flags = parser.parse_args(argv[1:])

  auth_path = os.path.dirname(filename)
  client_secrets_path = os.path.join(auth_path, CLIENT_SECRETS_FILE)
  service_account_path = os.path.join(auth_path, SERVICE_ACCOUNT_FILE)

  credentials = None
  if os.path.isfile(service_account_path):
    credentials = ServiceAccountCredentials.from_json_keyfile_name(
        service_account_path,
        scopes=API_SCOPE)
  elif os.path.isfile(client_secrets_path):
    message = tools.message_if_missing(client_secrets_path)
    flow = client.flow_from_clientsecrets(client_secrets_path,
                                          scope=API_SCOPE,
                                          message=message)
    storage_path = os.path.join(auth_path, SERVICE_NAME + '.dat')
    storage = Storage(storage_path)
    credentials = storage.get()
    if credentials is None or credentials.invalid:
      credentials = tools.run_flow(flow, storage, flags)
  else:
    print('No OAuth2 authentication files found. Checked:', file=sys.stderr)
    print('- %s' % service_account_path, file=sys.stderr)
    print('- %s' % client_secrets_path, file=sys.stderr)
    print('Please read the accompanying documentation.', file=sys.stderr)
    sys.exit(1)

  http = credentials.authorize(http=httplib2.Http())
  service = discovery.build(SERVICE_NAME, SERVICE_VERSION, http=http)
  return (service, flags)

unique_id_increment = 0


def get_unique_id():
  """Generates a unique ID.

  The ID is based on the current UNIX timestamp and a runtime increment.

  Returns:
    A unique string.
  """
  global unique_id_increment
  if unique_id_increment is None:
    unique_id_increment = 0
  unique_id_increment += 1
  return '%d%d' % (int(time.time()), unique_id_increment)
