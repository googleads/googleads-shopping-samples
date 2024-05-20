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

package shopping.merchant.samples.productsdatasourcesworkflow.v1beta;

import shopping.merchant.samples.datasources.v1beta.CreatePrimaryProductDataSourceWildCardSample;
import shopping.merchant.samples.products.v1beta.InsertProductInputSample;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to create a datasource and insert a product in the datasource. */
public class CreateDataSourceAndInsertFirstProductSample {

  // [START create_datasource_and_insert_first_product]
  public static void createDatasourceAndInsertFirstProduct(Config config, String displayName)
      throws Exception {

    // Insert the wildcard datasource that accepts all feedLabel contentLanguage combinations
    // and store its `name` to use to insert the product.
    String dataSourceName =
        CreatePrimaryProductDataSourceWildCardSample.createDataSource(config, displayName);

    System.out.println(
        "Waiting for 300 seconds so the new datasource can propagate in the system...");
    Thread.sleep(300000); // 300,000 milliseconds = 300 seconds

    InsertProductInputSample.insertProductInput(config, dataSourceName);
  }

  // [END create_datasource_and_insert_first_product]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The displayed data source name in the Merchant Center UI.
    String displayName = "British Primary Product Data For Workflow Example";

    createDatasourceAndInsertFirstProduct(config, displayName);
  }
}
