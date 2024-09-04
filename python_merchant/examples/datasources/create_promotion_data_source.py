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
"""A module to create a Promotion DataSource."""

# [START CreatePromotionDatasource]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_datasources_v1beta

# ENSURE you fill in the merchant account for the sample to work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"


def create_promotion_data_source():
  """Creates a `DataSource` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_datasources_v1beta.DataSourcesServiceClient(
      credentials=credentials
  )

  # Creates a PromotionDataSource.
  # Wildcards are not available for PromotionDataSources. PromotionDataSources
  # can only be created for a specific `targetCountry` and `contentLanguage`
  # combination.
  promotion_datasource = merchant_datasources_v1beta.PromotionDataSource()
  promotion_datasource.target_country = "CH"
  promotion_datasource.content_language = "fr"

  # Creates a DataSource and populates its attributes.
  data_source = merchant_datasources_v1beta.DataSource()
  data_source.display_name = "Example DataSource"
  data_source.promotion_data_source = promotion_datasource

  # Creates the request.
  request = merchant_datasources_v1beta.CreateDataSourceRequest(
      parent=_PARENT, data_source=data_source
  )

  # Makes the request and catches and prints any error messages.
  try:
    response = client.create_data_source(request=request)
    print(f"DataSource successfully created: {response}")
  except RuntimeError as e:
    print("DataSource creation failed")
    print(e)


# [END CreatePromotionDatasource]

if __name__ == "__main__":
  create_promotion_data_source()
