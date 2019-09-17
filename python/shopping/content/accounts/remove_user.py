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
"""Removes a user from the primary account."""

from __future__ import print_function
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  email = config.get('accountSampleUser')
  if not email:
    print('Must specify the user email to remove in the samples configuration.')
    sys.exit(1)

  # First we need to retrieve the existing set of users.
  account = service.accounts().get(
      merchantId=merchant_id, accountId=merchant_id).execute()

  users = account.get('users')
  if not users:
    print('No users in account %d.' % merchant_id)
    sys.exit(1)

  matched = [u for u in users if u['emailAddress'] == email]
  if not matched:
    print('User %s was not found.' % email)
    sys.exit(1)

  for u in matched:
    users.remove(u)
  account['users'] = users

  # Patch account with new user list.
  service.accounts().update(
      merchantId=merchant_id, accountId=merchant_id, body=account).execute()

  print('User %s was removed from merchant ID %d' % (email, merchant_id))


if __name__ == '__main__':
  main(sys.argv)
