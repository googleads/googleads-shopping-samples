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

"""Adds several products to the specified account, in a single batch."""

import argparse
import sys

from apiclient import sample_tools
from apiclient.http import BatchHttpRequest
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
# Number of products to insert.
BATCH_SIZE = 5
# Root URL of your site, claimed in merchant center
ROOT_URL = 'www.example.com'

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

  batch = { "entries": [ ] }

  for i in range(BATCH_SIZE):
    offer_id = 'book#%s' % shopping_common.get_unique_id()
    product = {
        'offerId': offer_id,
        'title': 'This is book number %d' % (i,),
        'description': 'Sample book',
        'link': 'http://%s/book.html' % ROOT_URL,
        'imageLink': 'http://my-book-shop.com/book.jpg',
        'contentLanguage': CONTENT_LANGUAGE,
        'targetCountry': TARGET_COUNTRY,
        'channel': CHANNEL,
        'availability': 'in stock',
        'condition': 'new',
        'googleProductCategory': 'Media > Books',
        'gtin': '9780007350896',
        'price': {'value': '%d.50' % (i,), 'currency': 'USD'},
        'shipping': [{
            'country': 'US',
            'service': 'Standard shipping',
            'price': {'value': '0.99', 'currency': 'USD'}
        }],
        'shippingWeight': {'value': '200', 'unit': 'grams'}
    }
    # Add product to the batch.
    batch['entries'].append({
      'batchId': i,
      'merchantId': merchant_id,
      'method': 'insert',
      'product': product
    })

  try:
    request = service.products().custombatch(body=batch)
    result = request.execute()

    if result['kind'] == 'content#productsCustomBatchResponse':
      entries = result['entries']
      for entry in entries:
        if 'product' in entry:
          product = entry['product']
          print ('Product with offerId "%s" and title "%s" was created.' %
                (product['offerId'], product['title']))
        elif 'errors' in entry:
          print entry['errors']
    else:
      print 'There was an error. Response: %s' % (result)
  except client.AccessTokenRefreshError:
    print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')

if __name__ == '__main__':
  main(sys.argv)
