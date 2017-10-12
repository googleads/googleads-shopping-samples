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

from __future__ import print_function
import argparse
import json
import sys

from shopping.content import common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'product_ids', nargs='*', help='The IDs of the products to update.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  product_ids = flags.product_ids

  new_status = {
      'availability': 'out of stock',
      'price': {
          'value': 3.14,
          'currency': 'USD'
      }
  }

  batch = {
      'entries': [{
          'batchId': i,
          'merchantId': merchant_id,
          'storeCode': v.split(':')[0],
          'productId': v,
          'inventory': new_status,
      } for i, v in enumerate(product_ids)],
  }

  request = service.inventory().custombatch(body=batch)
  result = request.execute()

  if result['kind'] == 'content#inventoryCustomBatchResponse':
    entries = result['entries']
    for entry in entries:
      errors = entry.get('errors')
      if errors:
        print('Errors for batch entry %d:' % entry['batchId'])
        print(json.dumps(errors, sort_keys=True, indent=2,
                         separators=(',', ': ')))
      else:
        print('Successfully performed inventory update for product "%s".' %
              (product_ids[entry['batchId']]))
  else:
    print('There was an error. Response: %s' % result)


if __name__ == '__main__':
  main(sys.argv)
