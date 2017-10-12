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
"""Gets the shipping settings of the specified account."""

from __future__ import print_function
import argparse
import sys

from shopping.content import common
from shopping.content.shippingsettings import sample

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

  settings = sample.create_shippingsettings_sample()
  service.shippingsettings().update(
      merchantId=merchant_id, accountId=merchant_id, body=settings).execute()
  status = service.shippingsettings().get(
      merchantId=merchant_id, accountId=merchant_id).execute()
  print('Account %s:' % status['accountId'])
  postal_groups = status.get('postalCodeGroups')
  if not postal_groups:
    print('- No postal code groups.')
  else:
    print('- %d postal code group(s):' % len(postal_groups))
  services = status.get('services')
  if not services:
    print('- No services.')
  else:
    print('- %d service(s):' % len(services))
    for service in services:
      print('  Service "%s":' % service['name'])
      print('  - Delivery country: %s' % service['deliveryCountry'])
      print('  - Currency: %s' % service['currency'])
      print('  - Active: %s' % service['active'])
      print('  - Delivery time: %d - %d days' %
            (service['deliveryTime']['minTransitTimeInDays'],
             service['deliveryTime']['maxTransitTimeInDays']))
      rate_groups = service.get('rateGroups')
      if not rate_groups:
        print('  - No rate groups.')
      else:
        print('  - %d rate group(s).' % len(rate_groups))


if __name__ == '__main__':
  main(sys.argv)
