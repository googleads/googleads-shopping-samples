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
import com.google.shopping.merchant.inventories.v1beta.DeleteLocalInventoryRequest;
import com.google.shopping.merchant.inventories.v1beta.LocalInventoryName;
import com.google.shopping.merchant.inventories.v1beta.LocalInventoryServiceClient;
import com.google.shopping.merchant.inventories.v1beta.LocalInventoryServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a Local inventory for a given product */
public class DeleteLocalInventorySample {

  // [START delete_local_inventory]
  public static void deleteLocalInventory(Config config, String productId, String storeCode)
      throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    LocalInventoryServiceSettings localInventoryServiceSettings =
        LocalInventoryServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    String name =
        LocalInventoryName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setProduct(productId)
            .setStoreCode(storeCode)
            .build()
            .toString();

    try (LocalInventoryServiceClient localInventoryServiceClient =
        LocalInventoryServiceClient.create(localInventoryServiceSettings)) {
      DeleteLocalInventoryRequest request =
          DeleteLocalInventoryRequest.newBuilder().setName(name).build();

      System.out.println("Sending deleteLocalInventory request");
      localInventoryServiceClient.deleteLocalInventory(request); // no response returned on success
      System.out.println(
          "Delete successful, note that it may take up to 30 minutes for the delete to update in"
              + " the system.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  // [END delete_local_inventory]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel:contentLanguage:feedLabel:offerId
    String productId = "local:en:label:1111111111";
    // The ID uniquely identifying each region.
    String storeCode = "EXAMPLE";

    deleteLocalInventory(config, productId, storeCode);
  }
}
