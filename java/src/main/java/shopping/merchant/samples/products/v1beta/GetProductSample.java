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
import com.google.shopping.merchant.products.v1beta.GetProductRequest;
import com.google.shopping.merchant.products.v1beta.Product;
import com.google.shopping.merchant.products.v1beta.ProductName;
import com.google.shopping.merchant.products.v1beta.ProductServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get a single product for a given Merchant Center account */
public class GetProductSample {

  // [START get_product]
  public static void getProduct(Config config, String productId) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductServiceSettings productServiceSettings =
        ProductServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates product name to identify product.
    String name =
        ProductName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setProduct(productId)
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (ProductServiceClient productServiceClient =
        ProductServiceClient.create(productServiceSettings)) {

      // The name has the format: accounts/{account}/products/{product}
      GetProductRequest request = GetProductRequest.newBuilder().setName(name).build();

      System.out.println("Sending get product request:");
      Product response = ProductServiceClient.getProduct(request);

      System.out.println("Retrieved Product below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_product]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel~contentLanguage~feedLabel~offerId
    String productId = "online~en~label~sku123";

    getProduct(config, productId);
  }
}
