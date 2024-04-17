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
import com.google.shopping.merchant.inventories.v1beta.DeleteRegionalInventoryRequest;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryName;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceClient;
import com.google.shopping.merchant.inventories.v1beta.RegionalInventoryServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a regional inventory for a given product */
public class DeleteRegionalInventorySample {

  // [START delete_regional_inventory]
  public static void deleteRegionalInventory(Config config, String productId, String regionId)
      throws Exception {
    // TODO(brothman): Please add more line comments to explain what each significant step is doing.
    // For example:
    // Obtains OAuth tokens based on the configuration.
    // Creates service settings using the credentials above. Etc

    GoogleCredentials credential = new Authenticator().authenticate();

    RegionalInventoryServiceSettings regionalInventoryServiceSettings =
        RegionalInventoryServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String name =
        RegionalInventoryName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setProduct(productId)
            .setRegion(regionId)
            .build()
            .toString();

    try (RegionalInventoryServiceClient regionalInventoryServiceClient =
        RegionalInventoryServiceClient.create(regionalInventoryServiceSettings)) {
      DeleteRegionalInventoryRequest request =
          DeleteRegionalInventoryRequest.newBuilder().setName(name).build();

      System.out.println("Sending deleteRegionalInventory request");
      regionalInventoryServiceClient.deleteRegionalInventory(
          request); // no response returned on success
      System.out.println(
          "Delete successful, note that it may take up to 30 minutes for the delete to update in"
              + " the system.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  // [END delete_regional_inventory]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel:contentLanguage:feedLabel:offerId
    String productId = "online:en:label:1111111111";
    // The ID uniquely identifying each region.
    String regionId = "1111111";

    deleteRegionalInventory(config, productId, regionId);
  }
}
