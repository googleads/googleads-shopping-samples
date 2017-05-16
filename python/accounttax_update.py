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
"""Updates the tax settings of the specified account."""

import argparse
import sys

import accounttax_sample
from oauth2client import client
import shopping_common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'account_id',
    type=int,
    help='The ID of the account for which to update information.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = shopping_common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  account_id = flags.account_id

  if merchant_id != account_id:
    shopping_common.check_mca(
        config,
        True,
        msg='Non-multi-client accounts can only set their own information.')

  try:
    settings = accounttax_sample.create_accounttax_sample(account_id)
    status = service.accounttax().update(
        merchantId=merchant_id, accountId=merchant_id, body=settings).execute()
    print 'Account %s:' % status['accountId']
    if shopping_common.json_absent_or_false(status, 'rules'):
      print '- No tax settings, so no tax is charged.'
    else:
      print '- Found %d tax rules:' % len(status['rules'])
      for issue in status['rules']:
        if not shopping_common.json_absent_or_false(issue, 'ratePercent'):
          print('  - For %s in %s: %s%%' %
                (issue['locationId'], issue['country'], issue['ratePercent']))
        if not shopping_common.json_absent_or_false(issue, 'useGlobalRate'):
          print('  - For %s in %s: using the global tax table rate.' %
                (issue['locationId'], issue['country']))
        if not shopping_common.json_absent_or_false(issue, 'shippingTaxed'):
          print '   NOTE: Shipping charges are also taxed.'
  except client.AccessTokenRefreshError:
    print('The credentials have been revoked or expired, please re-run the '
          'application to re-authorize')


if __name__ == '__main__':
  main(sys.argv)
