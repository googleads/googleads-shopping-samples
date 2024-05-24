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

package shopping.merchant.samples.accounts.termsofservices.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.RetrieveLatestTermsOfServiceRequest;
import com.google.shopping.merchant.accounts.v1beta.TermsOfService;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceKind;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceServiceClient;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to retrieve the latest TermsOfService a specific region and kind. */
public class RetrieveLatestTermsOfServiceSample {

  // [START retrieve_latest_termsofservice]
  public static void retrieveLatestTermsOfService(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    TermsOfServiceServiceSettings termsOfServiceServiceSettings =
        TermsOfServiceServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Calls the API and catches and prints any network failures/errors.
    try (TermsOfServiceServiceClient termsOfServiceServiceClient =
        TermsOfServiceServiceClient.create(termsOfServiceServiceSettings)) {

      RetrieveLatestTermsOfServiceRequest request =
          RetrieveLatestTermsOfServiceRequest.newBuilder()
              .setRegionCode("US")
              .setKind(TermsOfServiceKind.MERCHANT_CENTER)
              .build();

      System.out.println("Sending Retrieve Latest TermsOfService request:");
      TermsOfService response = termsOfServiceServiceClient.retrieveLatestTermsOfService(request);

      System.out.println("Retrieved TermsOfService below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END retrieve_latest_termsofservice]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    retrieveLatestTermsOfService(config);
  }
}
