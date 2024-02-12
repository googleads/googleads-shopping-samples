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
"""A module to delete a Regional Inventory."""

# [START DeleteRegionalInventory]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_inventories_v1beta

# ENSURE you fill in the merchant account and product ID and region ID
# for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PRODUCT = "[INSERT_PRODUCT_HERE]"
_REGION = "[INSERT_REGION_HERE]"
_NAME = f"accounts/{_ACCOUNT}/products/{_PRODUCT}/regionalInventories/{_REGION}"


def delete_regional_inventory():
  """Deletes the specified `RegionalInventory` resource from the given product.

  It might take up to an hour for the `RegionalInventory` to be deleted
  from the specific product. Once you have received a successful delete
  response, wait for that period before attempting a delete again.
  """

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_inventories_v1beta.RegionalInventoryServiceClient(
      credentials=credentials)

  # Creates the request.
  request = merchant_inventories_v1beta.DeleteRegionalInventoryRequest(
      name=_NAME,
  )

  # Makes the request and catch and print any error messages.
  try:
    client.delete_regional_inventory(request=request)
    print("Delete successful")
  except Exception as e:
    print("Delete failed")
    print(e)
# [END DeleteRegionalInventory]

if __name__ == "__main__":
  delete_regional_inventory()
