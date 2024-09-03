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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.shopping.merchant.inventories.v1beta.InsertLocalInventoryRequest;
import com.google.shopping.merchant.inventories.v1beta.LocalInventory;
import com.google.shopping.merchant.inventories.v1beta.LocalInventoryServiceClient;
import com.google.shopping.merchant.inventories.v1beta.LocalInventoryServiceSettings;
import com.google.shopping.merchant.products.v1beta.ListProductsRequest;
import com.google.shopping.merchant.products.v1beta.Product;
import com.google.shopping.merchant.products.v1beta.ProductsServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductsServiceClient.ListProductsPagedResponse;
import com.google.shopping.merchant.products.v1beta.ProductsServiceSettings;
import com.google.shopping.type.Channel.ChannelEnum;
import com.google.shopping.type.Price;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to insert Local inventory asynchronously for multiple products. Note,
 * this code sample will only work if you already have datasource and products available for local
 * products. You can create a datasource for local and products by looking at the DataSources and
 * Products samples.
 */
public class InsertLocalInventoryAsyncSample {

  // [START insert_local_inventory_async]
  /* Gets the names of all the local products for a given merchant center account. */
  public static List<String> getLocalProductNames(GoogleCredentials credential, String accountId)
      throws IOException {

    ProductsServiceSettings productsServiceSettings =
        ProductsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    try (ProductsServiceClient productsServiceClient =
        ProductsServiceClient.create(productsServiceSettings)) {

      ListProductsRequest request =
          ListProductsRequest.newBuilder()
              // Creates parent to identify the account from which to list all products.
              .setParent(String.format("accounts/%s", accountId))
              .build();

      System.out.println("Sending list products request:");
      ListProductsPagedResponse response = productsServiceClient.listProducts(request);

      // Iterates over all rows in all pages and prints the datasource in each row.
      // Automatically uses the `nextPageToken` if returned to fetch all pages of data
      List<String> localProductNames = new ArrayList<String>();
      for (Product product : response.iterateAll()) {

        // Filters for only local products, since only products with their channel set to "LOCAL"
        // can be used for local inventory.
        // The data feed must also have its channel set to "LOCAL_PRODUCTS".
        if (product.getChannel() == ChannelEnum.LOCAL) {

          // The name is returned in the format:
          // accounts/{account}/products/{channel}~{contentLanguage}~{feedLabel}~{offerId}
          // These will be used to insert local inventory for each product.
          localProductNames.add(product.getName());
        }
      }
      return localProductNames;
    }
  }

  public static void insertLocalInventoryAsync(
      GoogleCredentials credential, String accountId, String storeCode) throws Exception {

    LocalInventoryServiceSettings localInventoryServiceSettings =
        LocalInventoryServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    try (LocalInventoryServiceClient localInventoryServiceClient =
        LocalInventoryServiceClient.create(localInventoryServiceSettings)) {

      // In this example, we are simply using the same price for all products.
      Price price = Price.newBuilder().setAmountMicros(33_450_000).setCurrencyCode("USD").build();

      // Checks that the account has local products.
      List<String> localProductNames = getLocalProductNames(credential, accountId);
      if (localProductNames.isEmpty()) {
        throw new Exception("No local products found for this account.");
      }

      // Creates requests to update the inventory for each product, set a fixed price and set the
      // availability to "out of stock".
      List<InsertLocalInventoryRequest> requests =
          localProductNames.stream()
              .map(
                  name -> {
                    return InsertLocalInventoryRequest.newBuilder()
                        .setParent(name)
                        .setLocalInventory(
                            LocalInventory.newBuilder()
                                .setAvailability("out of stock")
                                .setStoreCode(storeCode)
                                .setPrice(price)
                                .build())
                        .build();
                  })
              .collect(Collectors.toList());

      // Inserts the local inventory for each product.
      System.out.println("Sending InsertLocalInventory requests");
      List<ApiFuture<LocalInventory>> futures =
          requests.stream()
              .map(
                  request ->
                      localInventoryServiceClient
                          .insertLocalInventoryCallable()
                          .futureCall(request))
              .collect(Collectors.toList());

      // Callback to handle the responses from the API once they are all returned.
      ApiFuture<List<LocalInventory>> responses = ApiFutures.allAsList(futures);
      ApiFutures.addCallback(
          responses,
          new ApiFutureCallback<List<LocalInventory>>() {
            @Override
            public void onSuccess(List<LocalInventory> results) {
              System.out.println("Inserted LocalInventory below");
              System.out.println(results);
            }

            @Override
            public void onFailure(Throwable throwable) {
              System.err.println(
                  "An error occurred while inserting local inventory: " + throwable.getMessage());
            }
          },
          MoreExecutors.directExecutor());
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

  // [END insert_local_inventory_async]

  public static void main(String[] args) throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();
    String accountId = Config.load().getAccountId().toString();
    // The code uniquely identifying each store.
    // This can be found in the Google Business Profile UI, by going to Business Profile Settings ->
    // Advanced Settings. The store must have passed verification for this code sample to work.
    String storeCode = "yourstorecode";
    insertLocalInventoryAsync(credential, accountId, storeCode);
  }
}
