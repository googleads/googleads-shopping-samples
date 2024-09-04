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
"""A module to update a DataSource."""

# [START UpdateDataSource]
from examples.authentication import generate_user_credentials
from google.protobuf import field_mask_pb2
from google.shopping import merchant_datasources_v1beta

# ENSURE you fill in the merchant account and datasource ID for the sample to
# work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
# An ID automatically assigned to the datasource after creation by Google.
_DATASOURCE = "[INSERT_DATASOURCE_HERE]"
_NAME = f"accounts/{_ACCOUNT}/dataSources/{_DATASOURCE}"


def update_data_source():
  """Updates the specified `DataSource` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_datasources_v1beta.DataSourcesServiceClient(
      credentials=credentials
  )

  # Creates a DataSource and populates its attributes.
  data_source = merchant_datasources_v1beta.DataSource()
  data_source.name = _NAME  # To identify the data source to update.
  data_source.display_name = "Example DataSource 2"

  # Sets field mask to include only the fields you want to update.
  field_mask = field_mask_pb2.FieldMask(paths=["display_name"])

  # Creates the request.
  request = merchant_datasources_v1beta.UpdateDataSourceRequest(
      data_source=data_source, update_mask=field_mask
  )

  # Makes the request and catch and print any error messages.
  try:
    client.update_data_source(request=request)
    print("Update successful")
  except RuntimeError as e:
    print("Update failed")
    print(e)


# [END UpdateDataSource]

if __name__ == "__main__":
  update_data_source()
