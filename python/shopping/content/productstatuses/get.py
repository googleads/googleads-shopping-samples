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
"""Gets the status of the specified product."""

from __future__ import print_function
import argparse
import json
import sys

from shopping.content import common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('product_id', help='The ID of the product to query.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  product_id = flags.product_id
  common.check_mca(config, False)

  status = service.productstatuses().get(
      merchantId=merchant_id, productId=product_id).execute()

  print('- Product "%s" with title "%s":' %
        (status['productId'], status['title']))
  print(json.dumps(status, sort_keys=True, indent=2, separators=(',', ': ')))


if __name__ == '__main__':
  main(sys.argv)
