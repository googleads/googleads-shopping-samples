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

package shopping.merchant.samples.products.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.products.v1beta.ListProductsRequest;
import com.google.shopping.merchant.products.v1beta.Product;
import com.google.shopping.merchant.products.v1beta.ProductServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductServiceClient.ListProductsPagedResponse;
import com.google.shopping.merchant.products.v1beta.ProductServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to list all the products for a given merchant center account */
public class ListProductsSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START list_products]
  public static void listProducts(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductServiceSettings productServiceSettings =
        ProductServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify the account from which to list all products.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (ProductServiceClient productServiceClient =
        ProductServiceClient.create(productServiceSettings)) {

      // The parent has the format: accounts/{account}
      ListProductsRequest request = ListProductsRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list products request:");
      ListProductsPagedResponse response = ProductServiceClient.listProducts(request);

      int count = 0;

      // Iterates over all rows in all pages and prints the product
      // in each row.
      // TODO(brothman): Confirm `response.iterateAll()` automatically uses the nextPageToken
      // To fetch all pages of data. And if confirmed, add a comment to let merchants know that.
      // And if not confirmed, update example to show how to use nextPageToken.
      for (Product element : response.iterateAll()) {
        System.out.println(element);
        count++;
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END list_products]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    listProducts(config);
  }
}
