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
"""Gets the status of all subaccounts for the specified account."""

from __future__ import print_function
import sys

from shopping.content import common

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  common.check_mca(config, True)

  request = service.accountstatuses().list(
      merchantId=merchant_id, maxResults=MAX_PAGE_SIZE)

  while request is not None:
    result = request.execute()
    statuses = result.get('resources')
    if not statuses:
      print('No statuses were returned.')
      break
    for status in statuses:
      print('Account %s:' % status['accountId'])
      issue_count = 0
      if 'products' in status:
        for product in status['products']:
          issues = product['itemLevelIssues']
          for issue in issues:
            issue_count += 1
            print('  - Issue: [%s] "%s" affecting %s items' %
                  (issue['code'],
                   issue.setdefault('detail', ''),
                   issue['numItems']))
      print('Total num of data quality issues: %d' % issue_count)
    request = service.accountstatuses().list_next(request, result)


if __name__ == '__main__':
  main(sys.argv)
