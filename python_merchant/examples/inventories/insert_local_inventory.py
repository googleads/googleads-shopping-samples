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
"""A module to insert a Local Inventory."""

# [START InsertLocalInventory]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_inventories_v1beta

# ENSURE you fill in the merchant account and product ID for the sample to
# work.
_ACCOUNT = "INSERT_ACCOUNT_HERE"
_PRODUCT = "INSERT_PRODUCT_HERE"
_PARENT = f"accounts/{_ACCOUNT}/products/{_PRODUCT}"
# ENSURE you fill in store code for the sample to work.
_STORE_CODE = "INSERT_STORE_CODE_HERE"


def insert_local_inventory():
  """Inserts a `LocalInventory` to a given product.

  Replaces the full `LocalInventory` resource if an entry with the same
  `region` already exists for the product.

  It might take up to 30 minutes for the new or updated `LocalInventory`
  resource to appear in products.
  """

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_inventories_v1beta.LocalInventoryServiceClient(
      credentials=credentials)

  # Creates a Local inventory and populate its attributes.
  local_inventory = merchant_inventories_v1beta.LocalInventory()
  local_inventory.store_code = _STORE_CODE
  local_inventory.availability = "in stock"
  local_inventory.price = {
      "currency_code": "USD",
      "amount_micros": 33450000,
  }

  # Creates the request.
  request = merchant_inventories_v1beta.InsertLocalInventoryRequest(
      parent=_PARENT,
      local_inventory=local_inventory,
  )

  # Makes the request and catch and print any error messages.
  try:
    response = client.insert_local_inventory(request=request)

    print("Insert successful")
    print(response)
  except Exception as e:
    print("Insert failed")
    print(e)
# [END InsertLocalInventory]

if __name__ == "__main__":
  insert_local_inventory()
