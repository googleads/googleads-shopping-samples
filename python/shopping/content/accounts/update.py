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
"""Updates the specified account on the specified account."""

from __future__ import print_function
import argparse
import sys

from shopping.content import common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('account_id', help='The ID of the account to update.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  account_id = flags.account_id
  common.check_mca(config, True)

  account = service.accounts().get(
      merchantId=merchant_id, accountId=account_id).execute()

  new_name = 'updated-account%s' % common.get_unique_id()
  account['name'] = new_name

  request = service.accounts().update(
      merchantId=merchant_id, accountId=account_id, body=account)

  result = request.execute()
  print('Account with id %s was updated with new name "%s".' %
        (account_id, result['name']))


if __name__ == '__main__':
  main(sys.argv)
