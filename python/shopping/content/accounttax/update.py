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

from __future__ import print_function
import argparse
import sys

from shopping.content import common
from shopping.content.accounttax import sample

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'account_id',
    nargs='?',
    default=0,
    type=int,
    help='The ID of the account for which to update information.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  account_id = flags.account_id

  if not account_id:
    account_id = merchant_id
  if merchant_id != account_id:
    common.check_mca(
        config,
        True,
        msg='Non-multi-client accounts can only set their own information.')

  settings = sample.create_accounttax_sample(account_id)
  status = service.accounttax().update(
      merchantId=merchant_id, accountId=merchant_id, body=settings).execute()
  print('Account %s:' % status['accountId'])
  rules = status.get('rules')
  if not rules:
    print('- No tax settings, so no tax is charged.')
    return
  print('- Found %d tax rules:' % len(rules))
  for rule in rules:
    rate_percent = rule.get('ratePercent')
    if rate_percent:
      print('  - For %s in %s: %s%%' %
            (rule['locationId'], rule['country'], rate_percent))
    use_global = rule.get('useGlobalRate')
    if use_global:
      print('  - For %s in %s: using the global tax table rate.' %
            (rule['locationId'], rule['country']))
    taxed_shipping = rule.get('shippingTaxed')
    if taxed_shipping:
      print('   NOTE: Shipping charges are also taxed.')


if __name__ == '__main__':
  main(sys.argv)
