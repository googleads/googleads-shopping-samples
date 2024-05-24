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
import com.google.shopping.merchant.accounts.v1beta.AccountName;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.DeleteAccountRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a given Merchant Center account. */
public class DeleteAccountSample {

  // [START delete_account]
  // This method can delete a standalone, MCA or sub-account. If you delete an MCA,
  // all sub-accounts will also be deleted.
  // Admin user access is required to execute this method.

  public static void deleteAccount(Config config, String accountId) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    AccountsServiceSettings accountsServiceSettings =
        AccountsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates account name to identify the account.
    String name =
        AccountName.newBuilder()
            .setAccount(accountId)
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (AccountsServiceClient accountsServiceClient =
        AccountsServiceClient.create(accountsServiceSettings)) {
      DeleteAccountRequest request = DeleteAccountRequest.newBuilder().setName(name).build();

      System.out.println("Sending Delete Account request");
      accountsServiceClient.deleteAccount(request); // No response returned on success.
      System.out.println("Delete successful.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END delete_account]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The ID of the MC account you want to delete.
    String accountId = "123456789";

    deleteAccount(config, accountId);
  }
}
