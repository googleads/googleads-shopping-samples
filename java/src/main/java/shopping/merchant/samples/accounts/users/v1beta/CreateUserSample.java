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
import com.google.shopping.merchant.accounts.v1beta.AccessRight;
import com.google.shopping.merchant.accounts.v1beta.CreateUserRequest;
import com.google.shopping.merchant.accounts.v1beta.User;
import com.google.shopping.merchant.accounts.v1beta.UserServiceClient;
import com.google.shopping.merchant.accounts.v1beta.UserServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to create a user for a Merchant Center account. */
public class CreateUserSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START create_user]
  public static void createUser(Config config, String email) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    UserServiceSettings userServiceSettings =
        UserServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to insert the user.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (UserServiceClient userServiceClient = UserServiceClient.create(userServiceSettings)) {

      CreateUserRequest request =
          CreateUserRequest.newBuilder()
              .setParent(parent)
              // This field is the email address of the user.
              .setUserId(email)
              .setUser(
                  User.newBuilder()
                      .addAccessRights(AccessRight.ADMIN)
                      .addAccessRights(AccessRight.PERFORMANCE_REPORTING)
                      .build())
              .build();

      System.out.println("Sending Create User request");
      User response = userServiceClient.createUser(request);
      System.out.println("Inserted User Name below");
      // The last part of the user name will be the email address of the user.
      // Format: `accounts/{account}/user/{user}`
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END create_user]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The email address of this user.
    String email = "testUser@gmail.com";

    createUser(config, email);
  }
}
