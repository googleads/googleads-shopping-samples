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
"""This class demonstrates how to create a regional inventory datasource."""

# [START CreateFileRegionalInventoryDatasource]
from examples.authentication import generate_user_credentials
from google.shopping.merchant_datasources_v1beta import CreateDataSourceRequest
from google.shopping.merchant_datasources_v1beta import DataSource
from google.shopping.merchant_datasources_v1beta import DataSourcesServiceClient
from google.shopping.merchant_datasources_v1beta import FileInput
from google.shopping.merchant_datasources_v1beta import RegionalInventoryDataSource

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def create_file_regional_inventory_data_source():
  """Creates a `DataSource` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = DataSourcesServiceClient(credentials=credentials)

  # If FetchSettings are not set, then this will be an `UPLOAD` file type
  # that you must manually upload via the Merchant Center UI.
  file_input = FileInput()
  file_input.file_name = (
      "British T-shirts Regional Inventory Data.txt"
  )

  # Creates a SupplementalProductDataSource.
  regional_inventory_datasource = RegionalInventoryDataSource()
  # As RegionalInventoryDataSources are a type of file feed, wildcards are not
  # available. RegionalInventoryDataSources can only be created for a specific
  # `feedLabel` and `contentLanguage` combination.
  regional_inventory_datasource.content_language = "en"
  regional_inventory_datasource.feed_label = "GB"

  # Creates a DataSource and populates its attributes.
  data_source = DataSource()
  data_source.display_name = "Example Regional Inventory DataSource"
  data_source.regional_inventory_data_source = regional_inventory_datasource
  data_source.file_input = file_input

  # Creates the request.
  request = CreateDataSourceRequest(parent=_PARENT, data_source=data_source)

  # Makes the request and catches and prints any error messages.
  try:
    response = client.create_data_source(request=request)
    print(f"DataSource successfully created: {response}")
  except RuntimeError as e:
    print("DataSource creation failed")
    print(e)


# [END CreateFileRegionalInventoryDatasource]

if __name__ == "__main__":
  create_file_regional_inventory_data_source()
