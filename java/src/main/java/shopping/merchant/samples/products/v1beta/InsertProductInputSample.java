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
import com.google.shopping.merchant.products.v1beta.InsertProductInputRequest;
import com.google.shopping.merchant.products.v1beta.ProductInput;
import com.google.shopping.merchant.products.v1beta.ProductInputServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductInputServiceSettings;
import com.google.shopping.merchant.products.v1beta.ProductShipping;
import com.google.shopping.type.Price;
import java.util.ArrayList;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to insert a product input */
public class InsertProductInputSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s/", accountId);
  }

  // [START insert_product_input]
  public static void insertProductInput(Config config, String dataSource) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductInputServiceSettings productInputServiceSettings =
        ProductInputServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to insert the product.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (ProductInputServiceClient productInputServiceClient =
        ProductInputServiceClient.create(productInputServiceSettings)) {

      // Price to be used for shipping ($33.45).
      Price price = Price.newBuilder().setAmountMicros(33_450_000).setCurrencyCode("USD").build();

      // Creates an array to store shipping data, then populate that array with shipping data.
      ArrayList<ProductShipping> shipping = new ArrayList<ProductShipping>();
      shipping.add(
          new ProductShipping().setPrice(price).setCountry("GB").setService("1st class post"));
      // TODO(brothman) Test to confirm this is right syntax for shipping when products bundle
      // is in devel.

      // The datasource can be either a primary or supplemental datasource w
      InsertProductInputRequest request =
          InsertProductInputRequest.newBuilder()
              .setParent(parent)
              // You can only insert products into datasource types of Input "API" and "FILE", and
              // of Type "Primary" or "Supplemental."
              .setDataSource(dataSource) // ToDo(brothman): Test to confirm whether this can take
              // a `name` for datasource, instead of just the ID field.
              // Also Jakub's suggestion to test if it can be called: `setDataSourceName`
              // If this product is already owned by another datasource, when re-inserting, the
              // new datasource will take ownership of the product.
              .setProductInput(
                  ProductInput.newBuilder()
                      .setChannel("online")
                      .setContentLanguage("en")
                      .setFeedLabel("label")
                      .setOfferId("sku123")
                      .setTitle("A Tale of Two Cities")
                      .setDescription("A classic novel about the French Revolution")
                      .setLink(websiteUrl + "/tale-of-two-cities.html")
                      .setImageLink(websiteUrl + "/tale-of-two-cities.jpg")
                      .setAvailability("in stock")
                      .setCondition("new")
                      .setGoogleProductCategory("Media > Books")
                      .setGtin("9780007350896")
                      .setShipping(shipping)
                      .build())
              .build();

      System.out.println("Sending insert ProductInput request");
      ProductInput response = productInputServiceClient.insertProductInput(request);
      System.out.println("Inserted ProductInput Name below");
      // The last part of the product name will be the product ID assigned to a product by Google.
      // Product ID has the format `channel~contentLanguage~feedLabel~offerId`
      System.out.println(response.getName());

      System.out.println("Inserted Product Name below");
      // TODO(brothman) - confirm if this is the correct syntax to get the final product name
      System.out.println(response.getProduct());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END insert_product_input]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // Identifies the data source that will own the product input
    String dataSource = "/accounts/{INSERT_ACCOUNT}/datasources/{INSERT_DATASOURCE_ID}";

    insertProductInput(config, dataSource);
  }
}
