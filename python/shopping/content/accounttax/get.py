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
"""Gets the tax settings of the specified account."""

from __future__ import print_function
import argparse
import sys

from shopping.content import common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'account_id',
    nargs='?',
    default=0,
    type=int,
    help='The ID of the account for which to get information.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  account_id = flags.account_id

  if not account_id:
    account_id = merchant_id
  elif merchant_id != account_id:
    common.check_mca(
        config,
        True,
        msg='Non-multi-client accounts can only get their own information.')

  status = service.accounttax().get(
      merchantId=merchant_id, accountId=merchant_id).execute()
  print('Account %s:' % status['accountId'])
  if common.json_absent_or_false(status, 'rules'):
    print('- No tax settings, so no tax is charged.')
  else:
    print('- Found %d tax rules:' % len(status['rules']))
    for issue in status['rules']:
      if not common.json_absent_or_false(issue, 'ratePercent'):
        print('  - For %s in %s: %s%%' %
              (issue['locationId'], issue['country'], issue['ratePercent']))
      if not common.json_absent_or_false(issue, 'useGlobalRate'):
        print('  - For %s in %s: using the global tax table rate.' %
              (issue['locationId'], issue['country']))
      if not common.json_absent_or_false(issue, 'shippingTaxed'):
        print('   NOTE: Shipping charges are also taxed.')


if __name__ == '__main__':
  main(sys.argv)
