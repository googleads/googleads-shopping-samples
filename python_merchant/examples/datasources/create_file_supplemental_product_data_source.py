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
"""This class demonstrates how to create a File Supplemental product datasource for the "en" and "GB" `feedLabel` and `contentLanguage` combination.

This supplemental feed is eligible to be linked to both a wildcard primary feed
and/or a primary feed with the same `feedLabel` and `contentLanguage`
combination.
"""

# [START CreateFileSupplementalProductDatasource]
from examples.authentication import generate_user_credentials
from google.shopping.merchant_datasources_v1beta import CreateDataSourceRequest
from google.shopping.merchant_datasources_v1beta import DataSource
from google.shopping.merchant_datasources_v1beta import DataSourcesServiceClient
from google.shopping.merchant_datasources_v1beta import FileInput
from google.shopping.merchant_datasources_v1beta import SupplementalProductDataSource

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def create_file_supplemental_product_data_source():
  """Creates a `DataSource` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = DataSourcesServiceClient(credentials=credentials)

  # If FetchSettings are not set, then this will be an `UPLOAD` file type
  # that you must manually upload via the Merchant Center UI or via SFTP.
  file_input = FileInput()
  file_input.file_name = (
      "British T-shirts Supplemental Product Data.txt"
  )

  # Creates a SupplementalProductDataSource.
  supplemental_datasource = SupplementalProductDataSource()
  supplemental_datasource.content_language = "en"
  supplemental_datasource.feed_label = "GB"

  # Creates a DataSource and populates its attributes.
  data_source = DataSource()
  data_source.display_name = "Example Supplemental DataSource"
  data_source.supplemental_product_data_source = supplemental_datasource
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


# [END CreateFileSupplementalProductDatasource]

if __name__ == "__main__":
  create_file_supplemental_product_data_source()
