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

package shopping.merchant.samples.products.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.products.v1beta.DeleteProductInputRequest;
import com.google.shopping.merchant.products.v1beta.ProductInputName;
import com.google.shopping.merchant.products.v1beta.ProductInputsServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductInputsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a product for a given Merchant Center account */
public class DeleteProductInputSample {

  // [START delete_product_input]
  public static void deleteProductInput(Config config, String productId, String dataSource)
      throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductInputsServiceSettings productInputsServiceSettings =
        ProductInputsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates product name to identify product.
    String name =
        ProductInputName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setProductinput(productId)
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (ProductInputsServiceClient productInputsServiceClient =
        ProductInputsServiceClient.create(productInputsServiceSettings)) {
      DeleteProductInputRequest request =
          DeleteProductInputRequest.newBuilder().setName(name).setDataSource(dataSource).build();

      System.out.println("Sending deleteProductInput request");
      productInputsServiceClient.deleteProductInput(request); // no response returned on success
      System.out.println(
          "Delete successful, note that it may take a few minutes for the delete to update in"
              + " the system. If you make a products.get or products.list request before a few"
              + " minutes have passed, the old product data may be returned.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END delete_product_input]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a product by Google. In the format
    // channel~contentLanguage~feedLabel~offerId
    String productId = "online~en~label~sku123";

    // The name of the dataSource from which to delete the product. If it is a primary feed, this
    // will delete the product completely. If it's a supplemental feed, it will only delete the
    // product information from that feed, but the product will still be available from the primary
    // feed.
    String dataSource = "accounts/{account}/dataSources/{dataSource}";

    deleteProductInput(config, productId);
  }
}
