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
import com.google.shopping.merchant.accounts.v1beta.Account;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceClient.ListAccountsPagedResponse;
import com.google.shopping.merchant.accounts.v1beta.AccountsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.ListAccountsRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to filter the accounts the user making the request has access to. */
public class FilterAccountsSample {

  // [START filter_accounts]
  public static void filterAccounts(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    AccountsServiceSettings accountsServiceSettings =
        AccountsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Calls the API and catches and prints any network failures/errors.
    try (AccountsServiceClient accountsServiceClient =
        AccountsServiceClient.create(accountsServiceSettings)) {

      // Filter for accounts with display names containing "store" and a provider with the ID "123":
      String filter = "accountName = \"*store*\" AND relationship(providerId = 123)";

      // Filter for all subaccounts of account "123":
      // String filter2 = "relationship(callerHasAccessToProvider() AND providerId = 123 AND
      // service(type = \"ACCOUNT_AGGREGATION\"))";

      // String filter3 = "relationship(service(handshakeState = \"APPROVED\" AND type =
      // \"ACCOUNT_MANAGEMENT\") AND providerId = 123)";

      ListAccountsRequest request = ListAccountsRequest.newBuilder().setFilter(filter).build();

      System.out.println("Sending list accounts request with filter:");
      ListAccountsPagedResponse response = accountsServiceClient.listAccounts(request);

      int count = 0;

      // Iterates over all rows in all pages and prints the sub-account
      // in each row.
      // `response.iterateAll()` automatically uses the `nextPageToken` and recalls the
      // request to fetch all pages of data.
      for (Account account : response.iterateAll()) {
        System.out.println(account);
        count++;
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END filter_accounts]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    filterAccounts(config);
  }
}
