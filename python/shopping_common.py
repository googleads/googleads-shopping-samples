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
"""Common utils for the Content API for Shopping samples."""

from __future__ import print_function
import argparse
import json
import os
import sys
import time
import urlparse

import _constants
import auth
import google_auth_httplib2
from googleapiclient import discovery
from googleapiclient import http


# Authenticate and return the Content API service along with any command-line
# flags/arguments.
def init(argv, doc, parents=None, sandbox=False):
  """A common initialization routine for the Content API samples.

  Args:
    argv: list of string, the command-line parameters of the application.
    doc: string, description of the application. Usually set to __doc__.
    parents: list of argparse.ArgumentParser, additional command-line flags.
    sandbox: boolean, whether to use the sandbox API endpoint or not.

  Returns:
    A tuple of (service, config, flags), where service is the service object,
    config is the configuration JSON in Python form, and flags
    are the parsed command-line flags.
  """
  service = None
  sandbox_service = None
  flags = None
  parent_parsers = []
  if parents is not None:
    parent_parsers.extend(parents)

  parser = argparse.ArgumentParser(
      description=doc,
      formatter_class=argparse.RawDescriptionHelpFormatter,
      parents=parent_parsers)
  parser.add_argument(
      '--config_path',
      metavar='PATH',
      default=os.path.expanduser('~/shopping-samples'),
      help='configuration directory for the Shopping samples')
  parser.add_argument(
      '--noconfig',
      action='store_true',
      help='run samples with no configuration directory')
  flags = parser.parse_args(argv[1:])

  config = {}
  if not flags.noconfig:
    if not os.path.isdir(flags.config_path):
      print(
          'Configuration directory "%s" does not exist.' % flags.config_path,
          file=sys.stderr)
      sys.exit(1)

    content_path = os.path.join(flags.config_path, 'content')
    if not os.path.isdir(content_path):
      print(
          'Content API configuration directory "%s" does not exist.' %
          content_path,
          file=sys.stderr)
      sys.exit(1)

    config_file = os.path.join(content_path, 'merchant-info.json')
    if not os.path.isfile(config_file):
      print('Configuration file %s does not exist.' % config_file)
      print('Falling back to configuration based on authenticated user.')
    else:
      config = json.load(open(config_file, 'r'))
    config['path'] = content_path

  credentials = auth.authorize(config)
  auth_http = google_auth_httplib2.AuthorizedHttp(
      credentials, http=http.set_user_agent(
          http.build_http(), _constants.APPLICATION_NAME))
  if _constants.ENDPOINT_ENV_VAR in os.environ:
    # Strip off everything after the host/port in the URL.
    root_url = urlparse.urljoin(os.environ[_constants.ENDPOINT_ENV_VAR], '/')
    print('Using non-standard root for API discovery: %s' % root_url)
    discovery_url = root_url + '/discovery/v1/apis/{api}/{apiVersion}/rest'
    service = discovery.build(
        _constants.SERVICE_NAME,
        _constants.SERVICE_VERSION,
        discoveryServiceUrl=discovery_url,
        http=auth_http)
    if sandbox:
      sandbox_service = discovery.build(
          _constants.SERVICE_NAME,
          _constants.SANDBOX_SERVICE_VERSION,
          discoveryServiceUrl=discovery_url,
          http=auth_http)
  else:
    service = discovery.build(
        _constants.SERVICE_NAME, _constants.SERVICE_VERSION, http=auth_http)
    if sandbox:
      sandbox_service = discovery.build(
          _constants.SERVICE_NAME,
          _constants.SANDBOX_SERVICE_VERSION,
          http=auth_http)

  # Now that we have a service object, fill in anything missing from the
  # configuration using API calls.
  retrieve_remaining_config_from_api(service, config)

  return (sandbox_service if sandbox else service, config, flags)


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


def retrieve_remaining_config_from_api(service, config):
  """Retrieves any missing configuration information using API calls.

  This function can fill in the following configuration fields:
  * merchantId

  It will also remove or overwrite existing values for the following fields:
  * isMCA
  * websiteUrl

  Args:
    service: Content API service object
    config: dictionary, Python representation of config JSON.
  """
  authinfo = service.accounts().authinfo().execute()
  if json_absent_or_false(authinfo, 'accountIdentifiers'):
    print('The currently authenticated user does not have access to '
          'any Merchant Center accounts.')
    sys.exit(1)
  if 'merchantId' not in config:
    first_account = authinfo['accountIdentifiers'][0]
    if json_absent_or_false(first_account, 'merchantId'):
      config['merchantId'] = int(first_account['aggregatorId'])
    else:
      config['merchantId'] = int(first_account['merchantId'])
    print('Using Merchant Center %d for running samples.' %
          config['merchantId'])
  merchant_id = config['merchantId']
  config['isMCA'] = False
  # The requested Merchant Center can only be an MCA if we are a
  # user of it (and thus have access) and it is listed as an
  # aggregator in authinfo.
  for account_id in authinfo['accountIdentifiers']:
    if ('aggregatorId' in account_id and
        int(account_id['aggregatorId']) == merchant_id):
      config['isMCA'] = True
      break
    if ('merchantId' in account_id and
        int(account_id['merchantId']) == merchant_id):
      break
  if config['isMCA']:
    print('Merchant Center %d is an MCA.' % config['merchantId'])
  else:
    print('Merchant Center %d is not an MCA.' % config['merchantId'])
  account = service.accounts().get(
      merchantId=merchant_id, accountId=merchant_id).execute()
  if not json_absent_or_false(account, 'websiteUrl'):
    config['websiteUrl'] = account['websiteUrl']
  elif 'websiteUrl' in config:
    del config['websiteUrl']
  if 'websiteUrl' not in config:
    print('No website for Merchant Center %d.' % config['merchantId'])
  else:
    print('Website for Merchant Center %d: %s' % (config['merchantId'],
                                                  config['websiteUrl']))


def check_mca(config, should_be_mca, msg=None):
  """Checks that the configured account is an MCA or not based on the argument.

  If not, it exits the program early.

  Args:
    config: dictionary, Python representation of config JSON.
    should_be_mca: boolean, whether or not we expect an MCA.
    msg: string, message to use instead of standard error message if desired.
  """
  is_mca = 'isMCA' in config and config['isMCA']

  if should_be_mca != is_mca:
    if msg is not None:
      print(msg)
    else:
      print('For this sample, you must%s use a multi-client account.' %
            ' not' if is_mca else '')
    sys.exit(1)


def json_absent_or_false(json_obj, key):
  """Checks if the key does not appear or maps to false in the JSON object.

  Our measure of false here is the same as Python.

  Args:
    json_obj: dictionary, Python representation of JSON.
    key: string, key to check for in the given JSON object.

  Returns:
    True if the key does not appear or maps to false, false otherwise.
  """
  return key not in json_obj or not json_obj[key]
