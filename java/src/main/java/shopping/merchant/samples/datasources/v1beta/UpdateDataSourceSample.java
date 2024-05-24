// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package shopping.merchant.samples.datasources.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.datasources.v1beta.DataSource;
import com.google.shopping.merchant.datasources.v1beta.DataSourceName;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.UpdateDataSourceRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update a datasource to change its name in the MC UI. */
public class UpdateDataSourceSample {

  // [START update_data_source]
  public static String updateDataSource(Config config, String displayName, String dataSourceId)
      throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates datasource name to identify datasource.
    String name =
        DataSourceName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setDatasource(dataSourceId)
            .build()
            .toString();

    DataSource dataSource =
        DataSource.newBuilder()
            // Update the datasource to have the new display name
            .setDisplayName(displayName)
            .setName(name)
            .build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("display_name").build();

    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {

      UpdateDataSourceRequest request =
          UpdateDataSourceRequest.newBuilder()
              .setDataSource(dataSource)
              .setUpdateMask(fieldMask)
              .build();

      System.out.println("Sending Update DataSource request");
      DataSource response = dataSourcesServiceClient.updateDataSource(request);
      System.out.println("Updated DataSource Name below");
      System.out.println(response.getName());
      return response.getName();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
      return null;
    }
  }

  // [END update_data_source]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The updated displayed datasource name in the Merchant Center UI.
    String displayName = "Great Britain Primary Product Data";

    // The ID of the datasource to update
    String dataSourceId = "11111111"; // Replace with your datasource ID.

    updateDataSource(config, displayName, dataSourceId);
  }
}
