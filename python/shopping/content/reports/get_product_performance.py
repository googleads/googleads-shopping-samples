#!/usr/bin/python
#
# Copyright 2022 Google Inc. All Rights Reserved.
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
"""Gets performance data of all products on the specified account."""

import json
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']

  # You can use relative date ranges or custom date ranges. This example
  # uses a relative date range.
  date_range = 'LAST_30_DAYS'

  # Choose the programs for which you want to get performance metrics.
  programs = "('FREE_PRODUCT_LISTING','SHOPPING_ADS')"

  query = f"""
          SELECT
            segments.title,
            segments.offer_id,
            metrics.impressions,
            metrics.clicks
          FROM MerchantPerformanceView
          WHERE segments.date DURING {date_range}
          AND segments.program IN {programs}
          ORDER BY metrics.clicks DESC
          """

  req_body = {
      'query': query
  }

  # Build request
  request = service.reports().search(
      merchantId=merchant_id,
      body=req_body)

  # Send request
  result = request.execute()

  # Check to ensure the result is not an empty object
  if bool(result):
    results = result['results']
    product_data = []

    # Extract product data from the request results
    for row in results:
      data = {}
      data['offer_id'] = row['segments'].get('offerId')
      data['title'] = row['segments'].get('title')
      data['impressions'] = row['metrics'].get('impressions')
      data['clicks'] = row['metrics'].get('clicks')
      product_data.append(data)

    # Convert product_data to a JSON string
    json_product_data = json.dumps(product_data)
    print('product_data:')
    print(json_product_data)
  else:
    print('Your search query returned no results.')

if __name__ == '__main__':
  main(sys.argv)