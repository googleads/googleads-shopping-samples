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
import com.google.shopping.merchant.datasources.v1beta.DataSource;
import com.google.shopping.merchant.datasources.v1beta.DataSourceName;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.GetDataSourceRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get a specific datasource for a given Merchant Center account. */
public class GetDataSourceSample {

  // [START get_datasource]
  public static DataSource getDataSource(Config config, String dataSourceId) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
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

    // Calls the API and catches and prints any network failures/errors.
    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {

      // The name has the format: accounts/{account}/datasources/{datasource}
      GetDataSourceRequest request = GetDataSourceRequest.newBuilder().setName(name).build();

      System.out.println("Sending GET DataSource request:");
      DataSource response = dataSourcesServiceClient.getDataSource(request);

      System.out.println("Retrieved DataSource below");
      System.out.println(response);
      return response;
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
      return null; // Necessary to satisfy the compiler as we're not returning a
      // DataSource on failure.
    }
  }

  // [END get_datasource]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a datasource by Google.
    String datasourceId = "1111111111"; // Replace with your datasource ID.

    getDataSource(config, datasourceId);
  }
}
