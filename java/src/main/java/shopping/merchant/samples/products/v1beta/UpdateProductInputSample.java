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
import com.google.shopping.merchant.products.v1beta.ProductInput;
import com.google.shopping.merchant.products.v1beta.ProductInputServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductInputServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update a product to change its title and availability. */
public class UpdateProductInput {

  // [START update_product]
  public static String updateProductInput(
      Config config,
      String productInputName,
      String newAvailability,
      String newTitle,
      String dataSource)
      throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    ProductInputServiceSettings productInputServiceSettings =
        ProductInputServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Create a product with the updated fields
    ProductInput productInput =
        ProductInput.newBuilder().setTitle(newTitle).setAvailability(newAvailability).build();

    try (ProductInputServiceClient productInputServiceClient =
        ProductInputServiceClient.create(productInputServiceSettings)) {

      UpdateProductRequest request =
          UpdateProductRequest.newBuilder()
              .setName(
                  productInputName) // Confirm this is how it's done for update requests in GAPIC
              .setProductInput(productInput)
              .setDataSource(dataSource)
              .setUpdateMask("title, availability")
              .build();

      System.out.println("Sending update Product request");
      Product response = productServiceClient.updateProduct(request);
      System.out.println("Updated Product Name below");
      System.out.println(response.getName());
      return response.getName();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_product]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    String productInputName = "INSERT_PRODUCT_INPUT_NAME_HERE";
    String newAvailability = "out of stock";
    String newTitle = "A Story of Three Villages";
    String dataSource = "INSERT_DATA_SOURCE_THAT_OWNS_PRODUCT_HERE";

    updateProductInput(config, productInputName, newAvailability, newTitle, dataSource);
  }
}
