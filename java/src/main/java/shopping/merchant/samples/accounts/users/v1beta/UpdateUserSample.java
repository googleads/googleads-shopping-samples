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
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.accounts.v1beta.AccessRight;
import com.google.shopping.merchant.accounts.v1beta.UpdateUserRequest;
import com.google.shopping.merchant.accounts.v1beta.User;
import com.google.shopping.merchant.accounts.v1beta.UserName;
import com.google.shopping.merchant.accounts.v1beta.UserServiceClient;
import com.google.shopping.merchant.accounts.v1beta.UserServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update a user to make it an admin of the MC account. */
public class UpdateUserSample {

  // [START update_user]
  public static void updateUser(Config config, String email, AccessRight accessRight)
      throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    UserServiceSettings userServiceSettings =
        UserServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates user name to identify user.
    String name =
        UserName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setEmail(email)
            .build()
            .toString();

    // Create a user with the updated fields.
    User user = User.newBuilder().setName(name).addAccessRights(accessRight).build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("access_rights").build();

    try (UserServiceClient userServiceClient = UserServiceClient.create(userServiceSettings)) {

      UpdateUserRequest request =
          UpdateUserRequest.newBuilder().setUser(user).setUpdateMask(fieldMask).build();

      System.out.println("Sending Update User request");
      User response = userServiceClient.updateUser(request);
      System.out.println("Updated User Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_user]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    String email = "testUser@gmail.com";
    // Give the user admin rights. Note that all other rights, like
    // PERFORMANCE_REPORTING, would be overwritten in this example
    // if the user had those access rights before the update.
    AccessRight accessRight = AccessRight.ADMIN;

    updateUser(config, email, accessRight);
  }
}
