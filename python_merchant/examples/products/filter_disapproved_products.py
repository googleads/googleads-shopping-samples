# -*- coding: utf-8 -*-
# Copyright 2024 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""A module to list and filter disapproved Products."""

# [START FilterDisapprovedProducts]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_products_v1beta

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def filter_disapproved_products():
  """Lists and filters the disapproved `Product` resources for a given account."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_products_v1beta.ProductsServiceClient(
      credentials=credentials
  )

  # Creates the request.
  request = merchant_products_v1beta.ListProductsRequest(parent=_PARENT)

  # Makes the request and catches and prints any error messages.
  try:
    response = client.list_products(request=request)
    disapproved_products = []
    for product in response.products:
      for destination in product.product_status.destination_statuses:
        # If the product is disapproved in any country for any destination.
        # Note that product.product_status also has an item_level_issues field
        # which can be used to filter products with specific issues.
        if destination.disapproved_countries:
          disapproved_products.append(product)
          break
    print(f"You have {len(disapproved_products)} disapproved products.")

  except RuntimeError as e:
    print("List request failed")
    print(e)


# [END FilterDisapprovedProducts]

if __name__ == "__main__":
  filter_disapproved_products()
