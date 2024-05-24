// Copyright 2024 Google LLC

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
import com.google.shopping.merchant.products.v1beta.GetProductRequest;
import com.google.shopping.merchant.products.v1beta.Product;
import com.google.shopping.merchant.products.v1beta.ProductsServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get a single product for a given Merchant Center account */
public class GetProductSample {

  // [START get_product]
  public static void getProduct(Config config, String product) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductsServiceSettings productsServiceSettings =
        ProductsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Calls the API and catches and prints any network failures/errors.
    try (ProductsServiceClient productsServiceClient =
        ProductsServiceClient.create(productsServiceSettings)) {

      // The name has the format: accounts/{account}/products/{productId}
      GetProductRequest request = GetProductRequest.newBuilder().setName(product).build();

      System.out.println("Sending get product request:");
      Product response = productsServiceClient.getProduct(request);

      System.out.println("Retrieved Product below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_product]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The name of the `product`, returned after a `Product.insert` request. We recommend
    // having stored this value in your database to use for all future requests.
    String product = "accounts/{datasource}/products/{productId}";

    getProduct(config, product);
  }
}
