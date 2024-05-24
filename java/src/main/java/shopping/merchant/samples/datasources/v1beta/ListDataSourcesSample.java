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
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient.ListDataSourcesPagedResponse;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.ListDataSourcesRequest;
import java.util.ArrayList;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to list all the datasources for a given Merchant Center account */
public class ListDataSourcesSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START list_data_sources]
  public static ArrayList<DataSource> listDataSources(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify the account from which to list all the datasources.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {

      // The parent has the format: accounts/{account}
      ListDataSourcesRequest request =
          ListDataSourcesRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list datasources request:");
      ListDataSourcesPagedResponse response = dataSourcesServiceClient.listDataSources(request);

      int count = 0;
      ArrayList<DataSource> dataSources = new ArrayList<DataSource>();
      ArrayList<DataSource> justPrimaryDataSources = new ArrayList<DataSource>();

      // Iterates over all rows in all pages and prints the datasource in each row.
      // Automatically uses the `nextPageToken` if returned to fetch all pages of data.
      for (DataSource element : response.iterateAll()) {
        System.out.println(element);
        count++;
        dataSources.add(element);
        // The below lines show how to filter datasources based on type.
        // `element.hasSupplementalProductDataSource()` would give you supplemental
        // datasources, etc.
        if (element.hasPrimaryProductDataSource()) {
          justPrimaryDataSources.add(element);
        }
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
      return dataSources;
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
      return null; // Necessary to satisfy the compiler as we're not returning an
      // ArrayList<DataSource> on failure.
    }
  }

  // [END list_data_sources]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    listDataSources(config);
  }
}
