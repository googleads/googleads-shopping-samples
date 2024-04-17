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

package shopping.merchant.samples.inventories.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.inventories.v1beta.ListRegionalInventoriesRequest;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventory;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceClient;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceClient.ListRegionalInventoriesPagedResponse;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to list all the regional inventories on a given product */
public class ListRegionalInventoriesSample {

  private static String getParent(String accountId, String productId) {
    return String.format("accounts/%s/products/%s", accountId, productId);
  }

  // [START list_regional_inventories]
  public static void listRegionalInventories(Config config, String productId) throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    RegionalInventoryServiceSettings regionalInventoryServiceSettings =
        RegionalInventoryServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String parent = getParent(config.getAccountId().toString(), productId);

    try (RegionalInventoryServiceClient regionalInventoryServiceClient =
        RegionalInventoryServiceClient.create(regionalInventoryServiceSettings)) {

      //  The parent product has the format: accounts/{account}/products/{product}
      ListRegionalInventoriesRequest request =
          ListRegionalInventoriesRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list regional inventory request:");
      ListRegionalInventoriesPagedResponse response =
          regionalInventoryServiceClient.listRegionalInventories(request);

      int count = 0;

      // Iterates over all rows in all pages and prints the regional inventory
      // in each row.
      for (RegionalInventory element : response.iterateAll()) {
        System.out.println(element);
        count++;
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  // [END list_regional_inventories]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel:contentLanguage:feedLabel:offerId
    String productId = "online:en:label:1111111111";
    listRegionalInventories(config, productId);
  }
}
