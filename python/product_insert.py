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

"""This example adds a product to a specified account."""

import argparse
import sys

from apiclient import sample_tools
from oauth2client import client
import shopping_common

# These constants define the identifiers for all of our example products/feeds.
#
# The products will be sold online.
CHANNEL = 'online'
# The product details are provided in English.
CONTENT_LANGUAGE = 'en'
# The products are sold in the USA.
TARGET_COUNTRY = 'US'

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
    offer_id = 'book#%s' % shopping_common.get_unique_id()
    product = {
        'offerId': offer_id,
        'title': 'A Tale of Two Cities',
        'description': 'A classic novel about the French Revolution',
        'link': 'http://my-book-shop.com/tale-of-two-cities.html',
        'imageLink': 'http://my-book-shop.com/tale-of-two-cities.jpg',
        'contentLanguage': CONTENT_LANGUAGE,
        'targetCountry': TARGET_COUNTRY,
        'channel': CHANNEL,
        'availability': 'in stock',
        'condition': 'new',
        'googleProductCategory': 'Media > Books',
        'gtin': '9780007350896',
        'price': {'value': '2.50', 'currency': 'USD'},
        'shipping': [{
            'country': 'US',
            'service': 'Standard shipping',
            'price': {'value': '0.99', 'currency': 'USD'}
        }],
        'shippingWeight': {'value': '200', 'unit': 'grams'}
    }

    # Add product.
    request = service.products().insert(merchantId=merchant_id,
                                        body=product)

    result = request.execute()
    print ('Product with offerId "%s" and title "%s" was created.' %
           (result['offerId'], result['title']))

  except client.AccessTokenRefreshError:
    print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')

if __name__ == '__main__':
  main(sys.argv)
