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
import com.google.shopping.merchant.datasources.v1beta.DataSourceReference;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceClient;
import com.google.shopping.merchant.datasources.v1beta.DataSourcesServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.PrimaryProductDataSource;
import com.google.shopping.merchant.datasources.v1beta.PrimaryProductDataSource.DefaultRule;
import com.google.shopping.merchant.datasources.v1beta.UpdateDataSourceRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to update a datasource to change its name in the MC UI. It also
 * demonstrates how to update a primary datasource to add supplemental datasources to its default
 * rule (https://support.google.com/merchants/answer/7450276).
 */
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

  public String updateDataSource(
      Config config,
      String primaryDataSourceName,
      String firstSupplementalDataSourceName,
      String secondSupplementalDataSourceName)
      throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    DataSourcesServiceSettings dataSourcesServiceSettings =
        DataSourcesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Setting self to 'true' refers to the primary datasource itself.
    DataSourceReference dataSourceReferenceSelf =
        DataSourceReference.newBuilder().setSelf(true).build();
    DataSourceReference firstSupplementalDataSourceReference =
        DataSourceReference.newBuilder()
            .setSupplementalDataSourceName(firstSupplementalDataSourceName)
            .build();
    DataSourceReference secondSupplementalDataSourceReference =
        DataSourceReference.newBuilder()
            .setSupplementalDataSourceName(secondSupplementalDataSourceName)
            .build();

    // The attributes will first be taken from the primary DataSource.
    // Then the first supplemental DataSource if the attribute is not in the primary DataSource
    // And finally the second supplemental DataSource if not in the first two DataSources.
    // Note that CustomRules could change the behavior of how updates are applied.
    DefaultRule defaultRule =
        DefaultRule.newBuilder()
            .addTakeFromDataSources(dataSourceReferenceSelf)
            .addTakeFromDataSources(firstSupplementalDataSourceReference)
            .addTakeFromDataSources(secondSupplementalDataSourceReference)
            .build();

    // The type of data that this datasource will receive.
    PrimaryProductDataSource primaryProductDataSource =
        PrimaryProductDataSource.newBuilder().setDefaultRule(defaultRule).build();

    DataSource dataSource =
        DataSource.newBuilder()
            // Update the primary datasource to have the default rule datasources in the correct
            // order.
            .setPrimaryProductDataSource(primaryProductDataSource)
            .setName(primaryDataSourceName)
            .build();

    // The '.' signifies a nested field.
    FieldMask fieldMask =
        FieldMask.newBuilder().addPaths("primary_product_data_source.default_rule").build();

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
