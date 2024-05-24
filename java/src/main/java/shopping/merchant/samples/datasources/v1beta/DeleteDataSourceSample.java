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
import com.google.shopping.merchant.datasources.v1beta.DataSourceName;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.DeleteDataSourceRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a datasource. */
public class DeleteDataSourceSample {

  // [START delete_data_source]
  public static void deleteDataSource(Config config, String dataSourceId) throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String name =
        DataSourceName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setDatasource(dataSourceId)
            .build()
            .toString();

    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {
      DeleteDataSourceRequest request = DeleteDataSourceRequest.newBuilder().setName(name).build();

      System.out.println("Sending deleteDataSource request");
      // Delete works for any datasource type.
      // If Type "Supplemental", delete will only work if it's not linked to any primary feed.
      // If a link exists and the Type is "Supplemental", you will need to remove the supplemental
      // feed from the default and/or custom rule(s) of any primary feed(s) that references it. Then
      // retry the delete.

      dataSourcesServiceClient.deleteDataSource(request); // No response returned on success.
      System.out.println(
          "Delete successful, note that it may take a few minutes for the delete to update in"
              + " the system.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END delete_data_source]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID automatically assigned to the datasource after creation by Google.
    String dataSourceId = "1111111111"; // Replace with your datasource ID.

    deleteDataSource(config, dataSourceId);
  }
}
