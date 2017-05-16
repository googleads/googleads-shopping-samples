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
"""Deletes several accounts from the specified account, in a single batch."""

import argparse
import sys

from apiclient.http import BatchHttpRequest
from oauth2client import client
import shopping_common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'account_ids', nargs='*', help='The IDs of the accounts to delete.')


def account_deleted(unused_request_id, unused_response, exception):
  if exception is not None:
    # Do something with the exception.
    print 'There was an error: ' + str(exception)
  else:
    print 'Account was deleted.'


def main(argv):
  # Authenticate and construct service.
  service, config, flags = shopping_common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  account_ids = flags.account_ids
  shopping_common.check_mca(config, True)

  batch = BatchHttpRequest(callback=account_deleted)

  for account_id in account_ids:
    # Add account deletion to the batch.
    batch.add(
        service.accounts().delete(merchantId=merchant_id, accountId=account_id))
  try:
    batch.execute()
  except client.AccessTokenRefreshError:
    print('The credentials have been revoked or expired, please re-run the '
          'application to re-authorize')


if __name__ == '__main__':
  main(sys.argv)
