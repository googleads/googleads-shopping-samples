#!/usr/bin/python
#
# Copyright 2017 Google Inc. All Rights Reserved.
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
"""Runs through a workflow involving all the methods in the Accounts service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common


def print_account(account):
  """Prints a representation of the given account."""
  print(json.dumps(account, sort_keys=True, indent=2, separators=(',', ': ')))


def common_workflow(service, config):
  """Performs the methods that can be used on any Merchant Center account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """
  # Just used to shorten later calls to the Accounts service
  acc = service.accounts()

  merchant_id = config['merchantId']
  email = config.get('accountSampleUser')
  google_ads_id = config.get('accountSampleAdWordsCID')

  # Retrieve the account information for the configured Merchant Center account.
  account = acc.get(merchantId=merchant_id, accountId=merchant_id).execute()
  print('Starting account information:')
  print_account(account)
  print()

  if email:
    new_user = {'emailAddress': email, 'admin': False}
    account['users'].append(new_user)
    account = acc.update(merchantId=merchant_id, accountId=merchant_id,
                         body=account).execute()
    print('After adding user:')
    print_account(account)
    print()

    # Keep users that have different emails.
    remaining = [u for u in account['users'] if u['emailAddress'] != email]
    account['users'] = remaining
    account = acc.update(
        merchantId=merchant_id, accountId=merchant_id, body=account).execute()
    print('After removing user:')
    print_account(account)
    print()

  if google_ads_id:
    google_ads_link = {'adsId': google_ads_id, 'status': 'active'}
    account.setdefault('adsLinks', []).append(google_ads_link)
    account = acc.update(merchantId=merchant_id, accountId=merchant_id,
                         body=account).execute()
    print('After adding Google Ads link:')
    print_account(account)
    print()

    # Only keep links for other Google Ads CIDs.
    # We need to make sure to do an integer comparison here, to match the
    # value we get from the configuration.
    remaining = [
        u for u in account['adsLinks'] if int(u['adsId']) != google_ads_id
    ]
    account['adsLinks'] = remaining
    account = acc.update(
        merchantId=merchant_id, accountId=merchant_id, body=account).execute()
    print('After removing Google Ads link:')
    print_account(account)
    print()


def mca_workflow(service, config, page_size=50):
  """Performs MCA-only methods from the Accounts service.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  acc = service.accounts()
  merchant_id = config['merchantId']

  count = 0
  print('Printing all sub-accounts:')
  request = acc.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    accounts = result.get('resources')
    if not accounts:
      print('No subaccounts found.')
      break
    count += len(accounts)
    for account in accounts:
      print_account(account)
    request = acc.list_next(request, result)
  print('%d accounts printed.' % count)

  name = 'account%s' % common.get_unique_id()
  account = {'name': name, 'websiteUrl': 'https://%s.example.com/' % name}

  print('Adding account %s... ' % name, end='')
  account = acc.insert(merchantId=merchant_id, body=account).execute()
  print('done.')
  account_id = int(account['id'])

  print('Retrieving (with retries) new account (ID %d).' % account_id)
  req = acc.get(merchantId=merchant_id, accountId=account_id)
  account = common.retry_request(req)

  print('Removing new account (ID %d)... ' % account_id, end='')
  acc.delete(merchantId=merchant_id, accountId=account_id).execute()
  print('done.')


def workflow(service, config):
  """Performs all possible Accounts methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Accounts workflow.')
  print()

  common_workflow(service, config)
  if common.is_mca(config):
    mca_workflow(service, config)

  print('Done with Accounts workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
