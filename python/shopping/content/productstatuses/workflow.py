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
"""Example workflow using all the methods in the Productstatuses service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common


def print_productstatus(status):
  """Prints a representation of the given product status."""
  print(json.dumps(status, sort_keys=True, indent=2, separators=(',', ': ')))


def non_mca_workflow(service, config, page_size=50):
  """Performs the methods that can be used on non-MCA accounts.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  # Just used to shorten later calls to the Productstatuses service
  ps = service.productstatuses()

  merchant_id = config['merchantId']

  count = 0
  print('Printing status of all products:')
  request = ps.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    statuses = result.get('resources')
    if not statuses:
      print('No product statuses returned.')
      break
    count += len(statuses)
    for status in statuses:
      print_productstatus(status)
    request = ps.list_next(request, result)
  print('Status for %d accounts printed.' % count)


def workflow(service, config):
  """Calls all possible Productstatuses methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Productstatuses workflow.')
  print()

  if common.is_mca(config):
    print('Nothing to do, as MCAs contain no products.\n')
  else:
    non_mca_workflow(service, config)

  print('Done with Productstatuses workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
