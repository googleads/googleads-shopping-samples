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
"""Example workflow using all the methods in the Accountstatuses service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common


def print_accountstatus(status):
  """Prints a representation of the given account status."""
  print(json.dumps(status, sort_keys=True, indent=2, separators=(',', ': ')))


def common_workflow(service, config):
  """Performs the methods that can be used on any Merchant Center account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """
  # Just used to shorten later calls to the Accountstatuses service
  acc = service.accountstatuses()

  merchant_id = config['merchantId']

  # Retrieve the account information for the configured Merchant Center account.
  status = acc.get(merchantId=merchant_id, accountId=merchant_id).execute()
  print('Status of account %d:' % merchant_id)
  print_accountstatus(status)
  print()


def mca_workflow(service, config, page_size=50):
  """Performs MCA-only methods from the Accountstatuses service.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  acc = service.accountstatuses()
  merchant_id = config['merchantId']

  count = 0
  print('Printing status of all sub-accounts:')
  request = acc.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    statuses = result.get('resources')
    if not statuses:
      print('No account statuses returned.')
      break
    count += len(statuses)
    for status in statuses:
      print_accountstatus(status)
    request = acc.list_next(request, result)
  print('Status for %d accounts printed.' % count)


def workflow(service, config):
  """Calls all possible Accountstatuses methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Accountstatuses workflow.')
  print()

  common_workflow(service, config)
  if common.is_mca(config):
    mca_workflow(service, config)

  print('Done with Accountstatuses workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
