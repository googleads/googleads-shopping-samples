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
"""Updates several products from the specified account.

Uses the inventory collection and a single batch. If you're updating any of
the supported properties in a product, be sure to use the inventory.set
method, for performance reasons.
"""

import argparse
import sys

from apiclient.http import BatchHttpRequest
from oauth2client import client
import shopping_common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'product_ids', nargs='*', help='The IDs of the products to update.')


def product_updated(request_id, unused_response, exception):
  if exception is not None:
    # Do something with the exception.
    print 'There was an error: ' + str(exception)
  else:
    print 'Request ID: %s - Product was updated.' % (str(request_id),)


def main(argv):
  # Authenticate and construct service.
  service, config, flags = shopping_common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  product_ids = flags.product_ids

  batch = BatchHttpRequest(callback=product_updated)

  for product_id in product_ids:
    new_status = {
        'availability': 'out of stock',
        'price': {
            'value': 3.14,
            'currency': 'USD'
        }
    }

    # Add product update to the batch.
    batch.add(service.inventory().set(
        merchantId=merchant_id,
        storeCode=product_id.split(':')[0],
        productId=product_id,
        body=new_status))
  try:
    batch.execute()

  except client.AccessTokenRefreshError:
    print('The credentials have been revoked or expired, please re-run the '
          'application to re-authorize')


if __name__ == '__main__':
  main(sys.argv)
