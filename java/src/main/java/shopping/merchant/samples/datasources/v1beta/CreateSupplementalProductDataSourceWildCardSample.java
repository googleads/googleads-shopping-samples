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
import com.google.shopping.merchant.datasources.v1beta.CreateDataSourceRequest;
import com.google.shopping.merchant.datasources.v1beta.DataSource;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.SupplementalProductDataSource;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to create a Supplemental product datasource all `feedLabel` and
 * `contentLanguage` combinations. This works only for API supplemental feeds.
 */
public class CreateSupplementalProductDataSourceWildCardSample {

  private static String getParent(String merchantId) {
    return String.format("accounts/%s", merchantId);
  }

  // [START create_supplemental_product_data_source_wildcard]
  public static String createDataSource(Config config, String displayName) throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String parent = getParent(config.getAccountId().toString());

    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {

      CreateDataSourceRequest request =
          CreateDataSourceRequest.newBuilder()
              .setParent(parent)
              .setDataSource(
                  DataSource.newBuilder()
                      .setDisplayName(displayName)
                      .setSupplementalProductDataSource(
                          SupplementalProductDataSource.newBuilder().build())
                      .build())
              .build();

      System.out.println("Sending create SupplementalProduct DataSource request");
      DataSource response = dataSourcesServiceClient.createDataSource(request);
      System.out.println("Created DataSource Name below");
      System.out.println(response.getName());
      return response.getName();
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
      return null; // Necessary to satisfy the compiler as we're not returning a
      // String on failure.
    }
  }

  // [END create_supplemental_product_data_source_wildcard]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The displayed datasource name in the Merchant Center UI.
    String displayName = "Supplemental API Product Data Wildcard";

    createDataSource(config, displayName);
  }
}
