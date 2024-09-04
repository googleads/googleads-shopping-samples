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
"""A module to list DataSources."""

# [START ListDataSources]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_datasources_v1beta

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def list_data_sources():
  """Lists the `DataSource` resources for a given account."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_datasources_v1beta.DataSourcesServiceClient(
      credentials=credentials
  )

  # Creates the request.
  request = merchant_datasources_v1beta.ListDataSourcesRequest(parent=_PARENT)

  # Makes the request and catch and print any error messages.
  try:
    response = client.list_data_sources(request=request)
    primary_data_sources = []
    for data_source in response.data_sources:
      # PrimaryProductDataSource is a oneOf field. If it is set, then this is a
      # primary data source.
      if data_source.primary_product_data_source:
        primary_data_sources.append(data_source)
    print(
        f"List request successful. You have {len(response.data_sources)} total"
        f" data sources, of which {len(primary_data_sources)} are primary data"
        " sources"
    )
  except RuntimeError as e:
    print("List request failed")
    print(e)


# [END ListDataSources]

if __name__ == "__main__":
  list_data_sources()
