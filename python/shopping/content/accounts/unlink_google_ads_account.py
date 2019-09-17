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
"""Unlinks the specified Google Ads account to the specified merchant center."""

from __future__ import print_function
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']
  google_ads_id = config.get('accountSampleAdWordsCID')
  if not google_ads_id:
    print('Must specify the Google Ads CID to unlink in the samples config.')
    sys.exit(1)

  # First we need to retrieve the existing set of users.
  account = service.accounts().get(
      merchantId=merchant_id, accountId=merchant_id).execute()

  google_ads_links = account.get('adsLinks')
  if not google_ads_links:
    print('No Google Ads accounts linked to account %d.' % merchant_id)
    sys.exit(1)

  # Do an integer comparison to match the version from the configuration.
  matched = [l for l in google_ads_links if int(l['adsId']) == google_ads_id]
  if not matched:
    print('Google Ads account %d was not linked.' % google_ads_id)
    sys.exit(1)

  for u in matched:
    google_ads_links.remove(u)
  account['adsLinks'] = google_ads_links

  # Patch account with new user list.
  service.accounts().update(
      merchantId=merchant_id, accountId=merchant_id, body=account).execute()

  print('Google Ads ID %d was removed from merchant ID %d' %
        (google_ads_id, merchant_id))


if __name__ == '__main__':
  main(sys.argv)
