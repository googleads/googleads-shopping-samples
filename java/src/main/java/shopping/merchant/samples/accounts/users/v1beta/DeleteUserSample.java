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
import com.google.shopping.merchant.accounts.v1beta.DeleteUserRequest;
import com.google.shopping.merchant.accounts.v1beta.UserName;
import com.google.shopping.merchant.accounts.v1beta.UserServiceClient;
import com.google.shopping.merchant.accounts.v1beta.UserServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a user for a given Merchant Center account. */
public class DeleteUserSample {

  // [START delete_user]
  public static void deleteUser(Config config, String email) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    UserServiceSettings userServiceSettings =
        UserServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates user name to identify the user.
    String name =
        UserName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setEmail(email)
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (UserServiceClient userServiceClient = UserServiceClient.create(userServiceSettings)) {
      DeleteUserRequest request = DeleteUserRequest.newBuilder().setName(name).build();

      System.out.println("Sending Delete User request");
      userServiceClient.deleteUser(request); // no response returned on success
      System.out.println("Delete successful.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END delete_user]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The email address of this user. If you want to delete the user information
    // Of the user making the Content API request, you can also use "me" instead
    // Of an email address.
    String email = "testUser@gmail.com";

    deleteUser(config, email);
  }
}
