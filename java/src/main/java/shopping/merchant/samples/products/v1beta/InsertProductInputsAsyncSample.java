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

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.shopping.merchant.products.v1beta.Attributes;
import com.google.shopping.merchant.products.v1beta.InsertProductInputRequest;
import com.google.shopping.merchant.products.v1beta.ProductInput;
import com.google.shopping.merchant.products.v1beta.ProductInputsServiceClient;
import com.google.shopping.merchant.products.v1beta.ProductInputsServiceSettings;
import com.google.shopping.merchant.products.v1beta.Shipping;
import com.google.shopping.type.Channel.ChannelEnum;
import com.google.shopping.type.Price;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to insert a product input */
public class InsertProductInputAsyncSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  private static String generateRandomString() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      sb.append(characters.charAt(random.nextInt(characters.length())));
    }
    return sb.toString();
  }

  private static ProductInput createRandomProduct() {
    Price price = Price.newBuilder().setAmountMicros(33_450_000).setCurrencyCode("USD").build();

    Shipping shipping =
        Shipping.newBuilder().setPrice(price).setCountry("GB").setService("1st class post").build();

    Shipping shipping2 =
        Shipping.newBuilder().setPrice(price).setCountry("FR").setService("1st class post").build();

    Attributes attributes =
        Attributes.newBuilder()
            .setTitle("A Tale of Two Cities")
            .setDescription("A classic novel about the French Revolution")
            .setLink("https://exampleWebsite.com/tale-of-two-cities.html")
            .setImageLink("https://exampleWebsite.com/tale-of-two-cities.jpg")
            .setAvailability("in stock")
            .setCondition("new")
            .setGoogleProductCategory("Media > Books")
            .setGtin("9780007350896")
            .addShipping(shipping)
            .addShipping(shipping2)
            .build();

    return ProductInput.newBuilder()
        .setChannel(ChannelEnum.ONLINE)
        .setContentLanguage("en")
        .setFeedLabel("CH")
        .setOfferId(generateRandomString())
        .setAttributes(attributes)
        .build();
  }

  // [START insert_product_input]
  public static void asyncInsertProductInput(Config config, String dataSource) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ProductInputsServiceSettings productInputsServiceSettings =
        ProductInputsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to insert the product.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (ProductInputsServiceClient productInputsServiceClient =
        ProductInputsServiceClient.create(productInputsServiceSettings)) {

      // Creates five insert product input requests with random product IDs.
      List<InsertProductInputRequest> requests = new ArrayList<>(5);
      for (int i = 0; i < 5; i++) {
        InsertProductInputRequest request =
            InsertProductInputRequest.newBuilder()
                .setParent(parent)
                // You can only insert products into datasource types of Input "API" and "FILE", and
                // of Type "Primary" or "Supplemental."
                // This field takes the `name` field of the datasource.
                .setDataSource(dataSource)
                // If this product is already owned by another datasource, when re-inserting, the
                // new datasource will take ownership of the product.
                .setProductInput(createRandomProduct())
                .build();

        requests.add(request);
      }

      System.out.println("Sending insert product input requests");
      List<ApiFuture<ProductInput>> futures =
          requests.stream()
              .map(
                  request ->
                      productInputsServiceClient.insertProductInputCallable().futureCall(request))
              .collect(Collectors.toList());

      // Creates callback to handle the responses when all are ready.
      ApiFuture<List<ProductInput>> responses = ApiFutures.allAsList(futures);
      ApiFutures.addCallback(
          responses,
          new ApiFutureCallback<List<ProductInput>>() {
            @Override
            public void onSuccess(List<ProductInput> results) {
              System.out.println("Inserted products below");
              System.out.println(results);
            }

            @Override
            public void onFailure(Throwable throwable) {
              System.out.println(throwable);
            }
          },
          MoreExecutors.directExecutor());

    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END insert_product_input]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // Identifies the data source that will own the product input.
    String dataSource = "accounts/" + config.getAccountId() + "/dataSources/{datasourceId}";

    asyncInsertProductInput(config, dataSource);
  }
}
