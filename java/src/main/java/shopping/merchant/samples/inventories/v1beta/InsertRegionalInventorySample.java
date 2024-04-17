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
import com.google.shopping.merchant.inventories.v1beta.InsertRegionalInventoryRequest;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventory;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceClient;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceSettings;
import com.google.shopping.type.Price;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to insert a regional inventory for a given product */
public class InsertRegionalInventorySample {

  private static String getParent(String accountId, String productId) {
    return String.format("accounts/%s/products/%s", accountId, productId);
  }

  // [START insert_regional_inventory]
  public static void insertRegionalInventory(Config config, String productId, String regionId)
      throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    RegionalInventoryServiceSettings regionalInventoryServiceSettings =
        RegionalInventoryServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String parent = getParent(config.getAccountId().toString(), productId);

    try (RegionalInventoryServiceClient regionalInventoryServiceClient =
        RegionalInventoryServiceClient.create(regionalInventoryServiceSettings)) {

      Price price = Price.newBuilder().setAmountMicros(33_450_000).setCurrencyCode("USD").build();

      InsertRegionalInventoryRequest request =
          InsertRegionalInventoryRequest.newBuilder()
              .setParent(parent)
              .setRegionalInventory(
                  RegionalInventory.newBuilder()
                      .setAvailability("out of stock")
                      .setRegion(regionId)
                      .setPrice(price)
                      .build())
              .build();

      System.out.println("Sending insert RegionalInventory request");
      RegionalInventory response = regionalInventoryServiceClient.insertRegionalInventory(request);
      System.out.println("Inserted RegionalInventory Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  // [END insert_regional_inventory]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel:contentLanguage:feedLabel:offerId
    String productId = "online:en:label:1111111111";
    // The ID uniquely identifying each region.
    String regionId = "1111111";
    insertRegionalInventory(config, productId, regionId);
  }
}
