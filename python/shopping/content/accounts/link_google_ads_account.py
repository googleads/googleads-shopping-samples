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
"""Links the specified Google Ads account to the specified merchant center account.
"""

from __future__ import print_function
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  google_ads_id = config.get('accountSampleAdWordsCID')
  if not google_ads_id:
    print(
        'Must specify the Google Ads CID to link in the samples configuration.')
    sys.exit(1)

  # First we need to retrieve the existing set of Google Ads links.
  response = service.accounts().get(
      merchantId=merchant_id, accountId=merchant_id).execute()

  account = response

  # Add new Google Ads link to existing Google Ads link list.
  google_ads_link = {'adsId': google_ads_id, 'status': 'active'}
  account.setdefault('adsLinks', []).append(google_ads_link)

  # Patch account with new Google Ads link list.
  response = service.accounts().update(
      merchantId=merchant_id, accountId=merchant_id, body=account).execute()

  print('Google Ads ID %d was added to merchant ID %d' %
        (google_ads_id, merchant_id))


if __name__ == '__main__':
  main(sys.argv)
