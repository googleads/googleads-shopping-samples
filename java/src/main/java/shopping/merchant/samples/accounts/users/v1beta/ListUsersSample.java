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

package shopping.merchant.samples.accounts.users.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.ListUsersRequest;
import com.google.shopping.merchant.accounts.v1beta.User;
import com.google.shopping.merchant.accounts.v1beta.UserServiceClient;
import com.google.shopping.merchant.accounts.v1beta.UserServiceClient.ListUsersPagedResponse;
import com.google.shopping.merchant.accounts.v1beta.UserServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to list all the users for a given Merchant Center account. */
public class ListUsersSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START list_users]
  public static void listUsers(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    UserServiceSettings userServiceSettings =
        UserServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify the account from which to list all users.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (UserServiceClient userServiceClient = UserServiceClient.create(userServiceSettings)) {

      // The parent has the format: accounts/{account}
      ListUsersRequest request = ListUsersRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list users request:");
      ListUsersPagedResponse response = userServiceClient.listUsers(request);

      int count = 0;

      // Iterates over all rows in all pages and prints the user
      // in each row.
      // `response.iterateAll()` automatically uses the `nextPageToken` and recalls the
      // request to fetch all pages of data.
      for (User element : response.iterateAll()) {
        System.out.println(element);
        count++;
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END list_users]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    listUsers(config);
  }
}
