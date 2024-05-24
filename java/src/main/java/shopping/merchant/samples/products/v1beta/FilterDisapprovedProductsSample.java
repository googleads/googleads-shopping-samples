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

// THIS ONE GETS A CANCELED ERROR STRANGELY ENOUGH - DEBUG:
// com.google.api.gax.rpc.CancelledException: io.grpc.StatusRuntimeException: CANCELLED: Failed to
// read message.

package shopping.merchant.samples.products.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.products.v1beta.ListProductsRequest;
import com.google.shopping.merchant.products.v1beta.Product;
import com.google.shopping.merchant.products.v1beta.ProductStatus.DestinationStatus;
import com.google.shopping.merchant.products.v1beta.ProductsServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductsServiceClient.ListProductsPagedResponse;
import com.google.shopping.merchant.products.v1beta.ProductsServiceSettings;
import java.util.ArrayList;
import java.util.List;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to get the list of all the disapproved products for a given merchant
 * center account.
 */
public class FilterDisapprovedProductsSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START filter_disapproved_products]
  public static void listProducts(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductsServiceSettings productsServiceSettings =
        ProductsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify the account from which to list all products.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (ProductsServiceClient productsServiceClient =
        ProductsServiceClient.create(productsServiceSettings)) {

      // The parent has the format: accounts/{account}
      ListProductsRequest request = ListProductsRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list products request:");
      System.out.println("Will filter through response for disapproved products.");
      ListProductsPagedResponse response = productsServiceClient.listProducts(request);

      ArrayList<Product> disapprovedProducts = new ArrayList<Product>();

      // Iterates over all rows in all pages.
      // Automatically uses the `nextPageToken` if returned to fetch all pages of data.
      // Creates a list of all products that are disapproved in at least one country
      // for at least one destination.
      for (Product product : response.iterateAll()) {

        List<DestinationStatus> destinationStatuses =
            product.getProductStatus().getDestinationStatusesList();

        // Filter through all the destinations and capture if the product is disapproved in any
        // country.
        for (DestinationStatus destinationStatus : destinationStatuses) {
          if (destinationStatus.getDisapprovedCountriesCount() > 0) {
            disapprovedProducts.add(product);
            break; // exit the inner loop, so we don't add the same product multiple times
            // if it's disapproved for different destinations.
          }
        }
      }
      System.out.print("The following count of disapproved products were returned: ");
      System.out.println(disapprovedProducts.size());
    } catch (Exception e) {
      System.out.println("An error has occured: ");
      System.out.println(e);
    }
  }

  // [END filter_disapproved_products]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    listProducts(config);
  }
}
