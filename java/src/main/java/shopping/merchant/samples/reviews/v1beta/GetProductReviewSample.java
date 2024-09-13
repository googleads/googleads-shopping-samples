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

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.reviews.v1beta.GetProductReviewRequest;
import com.google.shopping.merchant.reviews.v1beta.ProductReview;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewsServiceClient;
import com.google.shopping.merchant.reviews.v1beta.ProductReviewsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get a product review. */
public class GetProductReviewSample {

  // [START get_product_review]
  public static void getProductReview(String accountId, String productReviewId) throws Exception {
    GoogleCredentials credential = new Authenticator().authenticate();

    ProductReviewsServiceSettings productReviewsServiceSettings =
        ProductReviewsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    try (ProductReviewsServiceClient productReviewsServiceClient =
        ProductReviewsServiceClient.create(productReviewsServiceSettings)) {

      GetProductReviewRequest request =
          GetProductReviewRequest.newBuilder()
              .setName(String.format("accounts/%s/productReviews/%s", accountId, productReviewId))
              .build();

      System.out.println("Sending get product review request:");
      ProductReview response = productReviewsServiceClient.getProductReview(request);
      System.out.println("Product review retrieved successfully:");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_product_review]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    String productReviewId = "YOUR_PRODUCT_REVIEW_ID";
    getProductReview(config.getAccountId().toString(), productReviewId);
  }
}
