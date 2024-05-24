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

package shopping.merchant.samples.accounts.accounts.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.protobuf.Empty;
import com.google.shopping.merchant.accounts.v1beta.Account;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.CreateAndConfigureAccountRequest;
import com.google.shopping.merchant.accounts.v1beta.CreateAndConfigureAccountRequest.AddAccountService;
import com.google.type.TimeZone;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to create a sub-account under an MCA account. */
public class CreateSubAccountSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START create_sub_account]
  public static void createSubAccount(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    AccountsServiceSettings accountsServiceSettings =
        AccountsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent/provider to identify the MCA account into which to insert the subaccount.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (AccountsServiceClient accountsServiceClient =
        AccountsServiceClient.create(accountsServiceSettings)) {

      CreateAndConfigureAccountRequest request =
          CreateAndConfigureAccountRequest.newBuilder()
              .setAccount(
                  Account.newBuilder()
                      .setAccountName("Demo Business")
                      .setAdultContent(false)
                      .setTimeZone(TimeZone.newBuilder().setId("America/New_York").build())
                      .setLanguageCode("en-US")
                      .build())
              .addService(
                  AddAccountService.newBuilder()
                      .setProvider(parent)
                      .setAccountAggregation(Empty.getDefaultInstance())
                      .build())
              .build();

      System.out.println("Sending Create SubAccount request");
      Account response = accountsServiceClient.createAndConfigureAccount(request);
      System.out.println("Inserted Account Name below");
      // Format: `accounts/{account}
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END create_sub_account]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    createSubAccount(config);
  }
}
