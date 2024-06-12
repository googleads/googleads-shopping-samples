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
import com.google.shopping.merchant.accounts.v1beta.RetrieveForApplicationTermsOfServiceAgreementStateRequest;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementState;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementStateServiceClient;
import com.google.shopping.merchant.accounts.v1beta.TermsOfServiceAgreementStateServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to retrieve the latest TermsOfService agreement state for the
 * account.
 */
public class RetrieveForApplicationTermsOfServiceAgreementStateSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START retrieve_for_application_termsofservice_agreementstate]
  public static void retrieveForApplicationTermsOfServiceAgreementState(Config config)
      throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    TermsOfServiceAgreementStateServiceSettings termsOfServiceAgreementStateServiceSettings =
        TermsOfServiceAgreementStateServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to retrieve the application terms of service agreement
    // state.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (TermsOfServiceAgreementStateServiceClient termsOfServiceAgreementStateServiceClient =
        TermsOfServiceAgreementStateServiceClient.create(
            termsOfServiceAgreementStateServiceSettings)) {

      // The parent has the format: accounts/{account}
      RetrieveForApplicationTermsOfServiceAgreementStateRequest request =
          RetrieveForApplicationTermsOfServiceAgreementStateRequest.newBuilder()
              .setParent(parent)
              .build();

      System.out.println("Sending RetrieveForApplication TermsOfService Agreement request:");
      TermsOfServiceAgreementState response =
          termsOfServiceAgreementStateServiceClient
              .retrieveForApplicationTermsOfServiceAgreementState(request);

      System.out.println("Retrieved TermsOfServiceAgreementState below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END retrieve_for_application_termsofservice_agreementstate]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    retrieveForApplicationTermsOfServiceAgreementState(config);
  }
}
