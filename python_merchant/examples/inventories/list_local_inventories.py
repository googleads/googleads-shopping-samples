# -*- coding: utf-8 -*-
# Copyright 2023 Google LLC
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
"""A module to list Local Inventories."""

# [START ListLocalInventories]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_inventories_v1beta

# ENSURE you fill in the merchant account and product ID for the sample to
# work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PRODUCT = "[INSERT_PRODUCT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}/products/{_PRODUCT}"


def list_local_inventories():
  """Lists the `LocalInventory` resources for the given product.

  The response might contain fewer items than specified by
  `pageSize`. If `pageToken` was returned in previous request, it can be
  used to obtain additional results.

  `LocalInventory` resources are listed per product for a given account.
  """

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_inventories_v1beta.LocalInventoryServiceClient(
      credentials=credentials)

  # Creates the request.
  # Page size is set to the default value.
  request = merchant_inventories_v1beta.ListLocalInventoriesRequest(
      parent=_PARENT,
      page_size=25000
  )

  try:
    # Makes the request and catch and print any error messages.
    # If you are returned more responses than your page size, this code
    # will automatically re-call the service with the `pageToken` until all
    # responses are returned.
    page_result = client.list_local_inventories(request=request)

    # Print the response.
    for response in page_result:
      print(response)

  except Exception as e:
    print("List failed")
    print(e)
# [END ListLocalInventories]

if __name__ == "__main__":
  list_local_inventories()
