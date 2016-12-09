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

"""Adds several products to the specified account, in a single batch."""

import sys

from oauth2client import client
import product_sample
import shopping_common

# Number of products to insert.
BATCH_SIZE = 5


def main(argv):
  # Authenticate and construct service.
  service, config, _ = shopping_common.init(argv, __doc__)
  merchant_id = config['merchantId']

  batch = {'entries': []}

  for i in range(BATCH_SIZE):
    offer_id = 'book#%s' % shopping_common.get_unique_id()
    product = product_sample.create_product_sample(
        config,
        offer_id,
        title='This is book number %d' % (i,),
        price={'value': '%d.50' % (i,), 'currency': 'USD'})
    # Add product to the batch.
    batch['entries'].append({'batchId': i,
                             'merchantId': merchant_id,
                             'method': 'insert',
                             'product': product})

  try:
    request = service.products().custombatch(body=batch)
    result = request.execute()

    if result['kind'] == 'content#productsCustomBatchResponse':
      entries = result['entries']
      for entry in entries:
        if not shopping_common.json_absent_or_false(entry, 'product'):
          product = entry['product']
          print ('Product with offerId "%s" and title "%s" was created.' %
                 (product['offerId'], product['title']))
        elif not shopping_common.json_absent_or_false(entry, 'errors'):
          print entry['errors']
    else:
      print 'There was an error. Response: %s' % (result)
  except client.AccessTokenRefreshError:
    print ('The credentials have been revoked or expired, please re-run the '
           'application to re-authorize')

if __name__ == '__main__':
  main(sys.argv)
