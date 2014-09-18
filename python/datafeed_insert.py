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

"""This example adds a datafeed to a specified account."""

import argparse
import sys

import apiclient
from apiclient import sample_tools
from oauth2client import client
import shopping_common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'merchant_id',
    help='The ID of the merchant center.')


def main(argv):
  # Authenticate and construct service.
  service, flags = sample_tools.init(
      argv, 'content', 'v2', __doc__, __file__, parents=[argparser])
  merchant_id = flags.merchant_id

  try:
    name = 'feed%s' % shopping_common.get_unique_id()
    datafeed = {
        'name': name,
        'contentType': 'products',
        'attributeLanguage': 'en',
        'contentLanguage': 'en',
        'intendedDestinations': ['Shopping'],
        # The file name must be unique per account. We only use unique names in
        # these examples, so it's not an issue here.
        'fileName': name,
        'targetCountry': 'US',
        # You can schedule monthly, weekly or daily.
        #
        # Monthly - set day of month ('dayOfMonth') and hour ('hour')
        # Weekly - set day of week ('weekday') and hour ('hour')
        # Daily - set just the hour ('hour')
        'fetchSchedule': {
            'weekday': 'monday',
            'hour': 6,
            'timeZone': 'America/Los_Angeles',
            'fetchUrl': 'https://feeds.myshop.com/' + name
        },
        'format': {
            'fileEncoding': 'utf-8',
            'columnDelimiter': 'tab',
            'quotingMode': 'value quoting'
        }
    }

    # Add datafeed.
    request = service.datafeeds().insert(merchantId=merchant_id,
                                         body=datafeed)

    result = request.execute()

    print ('Datafeed with name "%s" and ID "%s" was created.' %
           (result['name'], result['id']))

  except client.AccessTokenRefreshError:
    print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')

if __name__ == '__main__':
  main(sys.argv)
