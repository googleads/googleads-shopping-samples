// Copyright 2023 Google LLC
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

package shopping.merchant.samples.reviews.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.datasources.v1beta.CreateDataSourceRequest;
import com.google.shopping.merchant.datasources.v1beta.DataSource;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.ProductReviewDataSource;
import java.io.IOException;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to create a product review data source. */
public class CreateProductReviewsDataSourceSample {

  // [START create_product_reviews_data_source]
  private static void createProductReviewsDataSource(String accountId) throws IOException {

    GoogleCredentials credential = new Authenticator().authenticate();

    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    try (DataSourcesServiceClient dataSourcesServiceClient =
        DataSourcesServiceClient.create(dataSourcesServiceSettings)) {
      CreateDataSourceRequest request =
          CreateDataSourceRequest.newBuilder()
              .setParent(String.format("accounts/%s", accountId))
              .setDataSource(
                  DataSource.newBuilder()
                      .setDisplayName("Product Reviews Data Source")
                      .setProductReviewDataSource(ProductReviewDataSource.newBuilder().build())
                      .build())
              .build();

      System.out.println("Creating product reviews data source...");
      DataSource dataSource = dataSourcesServiceClient.createDataSource(request);
      System.out.println(
          String.format("Datasource created successfully: %s", dataSource.getName()));
    } catch (Exception e) {
      System.out.println(e);
      System.exit(1);
    }
  }

  // [END create_product_reviews_data_source]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    createProductReviewsDataSource(config.getAccountId().toString());
  }
}
