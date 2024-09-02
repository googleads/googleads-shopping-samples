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
"""A module to list Products."""

# [START ListProducts]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_products_v1beta

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def list_products():
  """Lists the `Product` resources for a given account."""

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
    print(f"List request successful: {response}")
  except RuntimeError as e:
    print("List request failed")
    print(e)


# [END ListProducts]

if __name__ == "__main__":
  list_products()
