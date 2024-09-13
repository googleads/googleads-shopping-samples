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

package shopping.merchant.samples.reviews.v1beta;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.Timestamp;
import com.google.shopping.merchant.reviews.v1beta.InsertProductReviewRequest;
import com.google.shopping.merchant.reviews.v1beta.ProductReview;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewAttributes;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewAttributes.ReviewLink;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewAttributes.ReviewLink.Type;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewsServiceClient;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewsServiceSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to insert multiple product reviews asynchronously. */
public class InsertProductReviewsAsyncSample {

  // [START insert_product_reviews_async]
  private static String generateRandomString() {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      sb.append(characters.charAt(random.nextInt(characters.length())));
    }
    return sb.toString();
  }

  // Returns a product review with a random ID.
  private static ProductReview createProductReview(String accountId) {
    String productReviewId = generateRandomString();

    ProductReviewAttributes attributes =
        ProductReviewAttributes.newBuilder()
            .setTitle("Would not recommend!")
            .setContent("Not fantastic.")
            .setOverallMin(1)
            .setOverallMax(5)
            .setOverallValue(2)
            .setReviewTime(Timestamp.newBuilder().setSeconds(123456789).build())
            .addProductLink("exampleproducturl.com")
            .setReviewLink(
                ReviewLink.newBuilder()
                    .setLink("examplereviewurl.com")
                    // The review page contains only this single review.
                    .setType(Type.SINGLETON)
                    .build())
            .addGtins("9780007350896")
            .addGtins("9780007350897")
            .build();

    return ProductReview.newBuilder()
        .setName(String.format("accounts/%s/productReviews/%s", accountId, productReviewId))
        .setProductReviewId(productReviewId)
        .setAttributes(attributes)
        .build();
  }

  public static void asyncInsertProductReviews(String accountId, String dataSourceId)
      throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    ProductReviewsServiceSettings productReviewsServiceSettings =
        ProductReviewsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    try (ProductReviewsServiceClient productReviewsServiceClient =
        ProductReviewsServiceClient.create(productReviewsServiceSettings)) {

      // Arbitrarily creates five product reviews with random IDs.
      List<InsertProductReviewRequest> requests = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        InsertProductReviewRequest request =
            InsertProductReviewRequest.newBuilder()
                .setParent(String.format("accounts/%s", accountId))
                .setProductReview(createProductReview(accountId))
                // Must be a product reviews data source. In other words, a data source whose "type"
                // is ProductReviewDataSource.
                .setDataSource(String.format("accounts/%s/dataSources/%s", accountId, dataSourceId))
                .build();
        requests.add(request);
      }

      // Inserts the product reviews.
      List<ApiFuture<ProductReview>> futures =
          requests.stream()
              .map(
                  request ->
                      productReviewsServiceClient.insertProductReviewCallable().futureCall(request))
              .collect(Collectors.toList());

      // Creates callback to handle the responses when all are ready.
      ApiFuture<List<ProductReview>> responses = ApiFutures.allAsList(futures);
      ApiFutures.addCallback(
          responses,
          new ApiFutureCallback<List<ProductReview>>() {
            @Override
            public void onSuccess(List<ProductReview> results) {
              System.out.println("Inserted product reviews below:");
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

  // [END insert_product_reviews_async]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    asyncInsertProductReviews(config.getAccountId().toString(), "YOUR_DATA_SOURCE_ID");
  }
}
