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

import argparse
import sys

from oauth2client import client
import shippingsettings_sample
import shopping_common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'account_id',
    type=int,
    help='The ID of the account for which to get information.')


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
        msg='Non-multi-client accounts can only get their own information.')

  try:
    settings = shippingsettings_sample.create_shippingsettings_sample()
    service.shippingsettings().update(
        merchantId=merchant_id, accountId=merchant_id, body=settings).execute()
    status = service.shippingsettings().get(
        merchantId=merchant_id, accountId=merchant_id).execute()
    print 'Account %s:' % status['accountId']
    if shopping_common.json_absent_or_false(status, 'postalCodeGroups'):
      print '- No postal code groups.'
    else:
      print '- %d postal code group(s):' % len(status['postalCodeGroups'])
    if shopping_common.json_absent_or_false(status, 'services'):
      print '- No services.'
    else:
      print '- %d service(s):' % len(status['services'])
      for service in status['services']:
        print '  Service "%s":' % service['name']
        print '  - Delivery country: %s' % service['deliveryCountry']
        print '  - Currency: %s' % service['currency']
        print '  - Active: %s' % service['active']
        print('  - Delivery time: %d - %d days' %
              (service['deliveryTime']['minTransitTimeInDays'],
               service['deliveryTime']['maxTransitTimeInDays']))
        if shopping_common.json_absent_or_false(service, 'rateGroups'):
          print '  - No rate groups.'
        else:
          print '  - %d rate groups.' % len(service['rateGroups'])
  except client.AccessTokenRefreshError:
    print('The credentials have been revoked or expired, please re-run the '
          'application to re-authorize')


if __name__ == '__main__':
  main(sys.argv)
