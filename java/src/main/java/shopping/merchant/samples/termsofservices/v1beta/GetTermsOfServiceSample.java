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
import com.google.shopping.merchant.accounts.v1beta.GetTermsOfServiceRequest;
import com.google.shopping.merchant.accounts.v1beta.TermsOfService;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceName;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceServiceClient;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get a TermsOfService for a specific version. */
public class GetTermsOfServiceSample {

  // [START get_termsofservice]
  public static void getTermsOfService(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    TermsOfServiceServiceSettings termsOfServiceServiceSettings =
        TermsOfServiceServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates TermsOfService name to identify TermsOfService.
    String name = TermsOfServiceName.newBuilder().setVersion("132").build().toString();

    // Calls the API and catches and prints any network failures/errors.
    try (TermsOfServiceServiceClient termsOfServiceServiceClient =
        TermsOfServiceServiceClient.create(termsOfServiceServiceSettings)) {

      // The name has the format: termsOfService/{version}
      GetTermsOfServiceRequest request =
          GetTermsOfServiceRequest.newBuilder().setName(name).build();

      System.out.println("Sending Get TermsOfService request:");
      TermsOfService response = termsOfServiceServiceClient.getTermsOfService(request);

      System.out.println("Retrieved TermsOfService below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_termsofservice]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getTermsOfService(config);
  }
}
