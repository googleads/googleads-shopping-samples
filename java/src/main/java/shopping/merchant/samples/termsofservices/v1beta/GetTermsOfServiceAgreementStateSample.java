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
import com.google.shopping.merchant.accounts.v1beta.GetTermsOfServiceAgreementStateRequest;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementState;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementStateName;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementStateServiceClient;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementStateServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to get a TermsOfServiceAgreementState for a specific
 * TermsOfServiceKind and country.
 */
public class GetTermsOfServiceAgreementStateSample {

  // [START get__termsofservice_agreementstate]
  public static void getTermsOfServiceAgreementState(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    TermsOfServiceAgreementStateServiceSettings termsOfServiceAgreementStateServiceSettings =
        TermsOfServiceAgreementStateServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates TermsOfServiceAgreementState name to identify TermsOfServiceAgreementState.
    String name =
        TermsOfServiceAgreementStateName.newBuilder()
            .setAccount(config.getAccountId().toString())
            // The Identifier is: "{TermsOfServiceKind}-{country}"
            .setIdentifier("MERCHANT_CENTER-US")
            .build()
            .toString();

    System.out.println(name);

    // Calls the API and catches and prints any network failures/errors.
    try (TermsOfServiceAgreementStateServiceClient termsOfServiceAgreementStateServiceClient =
        TermsOfServiceAgreementStateServiceClient.create(
            termsOfServiceAgreementStateServiceSettings)) {

      // The name has the format:
      // accounts/{account}/termsOfServiceAgreementStates/{TermsOfServiceKind}-{country}
      GetTermsOfServiceAgreementStateRequest request =
          GetTermsOfServiceAgreementStateRequest.newBuilder().setName(name).build();

      System.out.println("Sending Get TermsOfServiceAgreementState request:");
      TermsOfServiceAgreementState response =
          termsOfServiceAgreementStateServiceClient.getTermsOfServiceAgreementState(request);

      System.out.println("Retrieved TermsOfServiceAgreementState below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get__termsofservice_agreementstate]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getTermsOfServiceAgreementState(config);
  }
}
