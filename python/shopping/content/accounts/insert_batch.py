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
"""Adds accounts to the specified multi-client account, in a single batch."""

from __future__ import absolute_import
from __future__ import print_function
import json
import sys

from shopping.content import common
from six.moves import range

# Number of accounts to insert.
BATCH_SIZE = 5


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  common.check_mca(config, True)

  account_names = [
      'account%s' % common.get_unique_id() for i in range(BATCH_SIZE)
  ]
  batch = {
      'entries': [{
          'batchId': i,
          'merchantId': merchant_id,
          'method': 'insert',
          'account': {
              'name': v,
              'websiteUrl': 'https://%s.example.com/' % v,
          },
      } for i, v in enumerate(account_names)],
  }

  request = service.accounts().custombatch(body=batch)
  result = request.execute()

  if result['kind'] == 'content#accountsCustomBatchResponse':
    for entry in result['entries']:
      account = entry.get('account')
      errors = entry.get('errors')
      if account:
        print('Account %s with name "%s" was created.' %
              (account['id'], account['name']))
      elif errors:
        print('Errors for batch entry %d:' % entry['batchId'])
        print(json.dumps(errors, sort_keys=True, indent=2,
                         separators=(',', ': ')))
  else:
    print('There was an error. Response: %s' % result)


if __name__ == '__main__':
  main(sys.argv)
