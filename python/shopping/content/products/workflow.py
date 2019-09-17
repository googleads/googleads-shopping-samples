#!/usr/bin/python
#
# Copyright 2017 Google Inc. All Rights Reserved.
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
"""Example workflow using all the methods in the Products service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common
from shopping.content.products import sample


def print_product(product):
  """Prints a representation of the given product."""
  print(json.dumps(product, sort_keys=True, indent=2, separators=(',', ': ')))


def non_mca_workflow(service, config, page_size=50):
  """Performs the methods that can be used on non-MCA accounts.


  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  # Just used to shorten later calls to the Products service
  pr = service.products()

  merchant_id = config['merchantId']

  # List all products
  count = 0
  print('Printing status of all products:')
  request = pr.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    products = result.get('resources')
    if not products:
      print('No products returned.')
      break
    count += len(products)
    for product in products:
      # Get product.
      product_id = product['id']
      print('Getting product (ID "%s").' % product_id)
      info = pr.get(merchantId=merchant_id, productId=product_id).execute()
      print_product(info)
      print()
    request = pr.list_next(request, result)
  print('Status for %d products printed.\n' % count)

  offer_id = 'book#%s' % common.get_unique_id()
  product = sample.create_product_sample(config, offer_id)

  # Add product.
  print('Inserting product "%s":' % offer_id, end='')
  new_product = pr.insert(merchantId=merchant_id, body=product).execute()
  print('done.\n')
  product_id = new_product['id']

  # Delete product.
  print('Deleting product "%s"...' % offer_id, end='')
  pr.delete(merchantId=merchant_id, productId=product_id).execute()
  print('done.\n')


def workflow(service, config):
  """Calls all possible Products methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Products workflow.')
  print()

  if common.is_mca(config):
    print('Nothing to do, as MCAs contain no products.\n')
  else:
    non_mca_workflow(service, config)

  print('Done with Products workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
