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
"""A module to delete a Product Input."""

# [START DeleteProductInput]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_products_v1beta

# ENSURE you fill in the merchant account, product ID and data source for the
# sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
# In the format of `channel~contentLanguage~feedLabel~offerId`
_PRODUCT = "[INSERT_PRODUCT_HERE]"
_DATA_SOURCE = "[INSERT_DATA_SOURCE_HERE]"
_NAME = f"accounts/{_ACCOUNT}/productInputs/{_PRODUCT}"
_DATA_SOURCE_NAME = f"accounts/{_ACCOUNT}/dataSources/{_DATA_SOURCE}"


def delete_product_input():
  """Deletes the specified `ProductInput` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_products_v1beta.ProductInputsServiceClient(
      credentials=credentials
  )

  # Creates the request.
  request = merchant_products_v1beta.DeleteProductInputRequest(
      name=_NAME, data_source=_DATA_SOURCE_NAME
  )

  # Makes the request and catch and print any error messages.
  try:
    client.delete_product_input(request=request)
    print("Deletion successful")
  except RuntimeError as e:
    print("Deletion failed")
    print(e)


# [END DeleteProductInput]

if __name__ == "__main__":
  delete_product_input()
