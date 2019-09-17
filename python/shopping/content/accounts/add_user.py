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
"""Adds a user to the primary account."""

from __future__ import print_function
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  email = config.get('accountSampleUser')
  if not email:
    print('Must specify the user email to add in the samples configuration.')
    sys.exit(1)

  # First we need to retrieve the existing set of users.
  response = service.accounts().get(
      merchantId=merchant_id, accountId=merchant_id).execute()

  account = response

  # Add new user to existing user list.
  new_user = {'emailAddress': email, 'admin': False}
  account['users'].append(new_user)

  # Update account with new user list.
  response = service.accounts().update(
      merchantId=merchant_id, accountId=merchant_id, body=account).execute()

  print('Account %s was added to merchant ID %d' % (email, merchant_id))


if __name__ == '__main__':
  main(sys.argv)
