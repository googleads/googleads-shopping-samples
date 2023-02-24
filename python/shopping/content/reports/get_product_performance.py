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

# You can use relative date ranges or custom date ranges. This example
# uses a relative date range.
_DATE_RANGE = "LAST_30_DAYS"

# Choose the programs for which you want to get performance metrics.
_PROGRAMS = "('FREE_PRODUCT_LISTING','SHOPPING_ADS')"


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config["merchantId"]

  req_body = {
      "query": f"""
        SELECT
          segments.title,
          segments.offer_id,
          metrics.impressions,
          metrics.clicks
        FROM MerchantPerformanceView
        WHERE segments.date DURING {_DATE_RANGE}
        AND segments.program IN {_PROGRAMS}
        ORDER BY metrics.clicks DESC
        """
  }

  # Build request.
  request = service.reports().search(
      merchantId=merchant_id,
      body=req_body)

  # Send request.
  result = request.execute()

  # Check to ensure the result is not an empty object.
  if result:
    results = result["results"]
    product_data = []

    # Extract product data from the request results.
    for row in results:
      data = {
          "prod_id": row["segments"]["offerId"],
          "prod_title": row["segments"]["title"],
          "impressions": row["metrics"]["impressions"],
          "clicks": row["metrics"]["clicks"]
      }
      product_data.append(data)

    print(f'product_data: "{json.dumps(product_data)}".')
  else:
    print("Your search query returned no results.")

if __name__ == "__main__":
  main(sys.argv)
