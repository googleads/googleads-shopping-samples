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
"""Example workflow using all the methods in the Shippingsettings service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common
from shopping.content.shippingsettings import sample


def print_shippingsettings(settings):
  """Prints a representation of the given shipping settings."""
  print(json.dumps(settings, sort_keys=True, indent=2, separators=(',', ': ')))


def common_workflow(service, config):
  """Performs the methods that can be used on any Merchant Center account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """
  # Just used to shorten later calls to the Shippingsettings service
  ss = service.shippingsettings()

  merchant_id = config['merchantId']

  # Retrieve the shipping information for the configured Merchant Center
  # account.
  settings = ss.get(merchantId=merchant_id, accountId=merchant_id).execute()
  print('Shipping settings for account %d:' % merchant_id)
  print_shippingsettings(settings)
  print()

  changes = sample.create_shippingsettings_sample()
  new_settings = ss.update(
      merchantId=merchant_id, accountId=merchant_id, body=changes).execute()
  print('Changed account settings to sample settings:')
  print_shippingsettings(new_settings)
  print()

  updated = ss.update(
      merchantId=merchant_id, accountId=merchant_id, body=settings).execute()
  print('Replaced changes with old settings:')
  print_shippingsettings(updated)
  print()


def mca_workflow(service, config, page_size=50):
  """Performs MCA-only methods from the Shippingsettings service.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  ss = service.shippingsettings()
  merchant_id = config['merchantId']

  count = 0
  print('Printing shipping settings of all sub-accounts:')
  request = ss.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    all_settings = result.get('resources')
    if not all_settings:
      print('No shipping settings returned.')
      break
    count += len(all_settings)
    for settings in all_settings:
      print_shippingsettings(settings)
    request = ss.list_next(request, result)
  print('Shipping settings for %d accounts printed.' % count)


def workflow(service, config):
  """Calls all possible Shippingsettings methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Shippingsettings workflow.')
  print()

  common_workflow(service, config)
  if common.is_mca(config):
    mca_workflow(service, config)

  print('Done with Shippingsettings workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
