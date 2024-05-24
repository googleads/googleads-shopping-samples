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

package shopping.merchant.samples.accounts.emailpreferences.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferences;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferences.OptInState;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesName;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesServiceClient;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.UpdateEmailPreferencesRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/**
 * This class demonstrates how to update a EmailPreferences to OPT_IN to News and Tips. This service
 * only permits retrieving and updating email preferences for the authenticated user.
 */
public class UpdateEmailPreferencesSample {

  // [START update_email_preferences]
  public static void updateEmailPreferences(Config config, String email) throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    EmailPreferencesServiceSettings emailPreferencesServiceSettings =
        EmailPreferencesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates EmailPreferences name to identify EmailPreferences.
    String name =
        EmailPreferencesName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setEmail(email)
            .build()
            .toString();

    // Create a EmailPreferences with the updated fields.
    EmailPreferences emailPreferences =
        EmailPreferences.newBuilder().setName(name).setNewsAndTips(OptInState.OPTED_IN).build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("news_and_tips").build();

    try (EmailPreferencesServiceClient emailPreferencesServiceClient =
        EmailPreferencesServiceClient.create(emailPreferencesServiceSettings)) {

      UpdateEmailPreferencesRequest request =
          UpdateEmailPreferencesRequest.newBuilder()
              .setEmailPreferences(emailPreferences)
              .setUpdateMask(fieldMask)
              .build();

      System.out.println("Sending Update EmailPreferences request");
      EmailPreferences response = emailPreferencesServiceClient.updateEmailPreferences(request);
      System.out.println("Updated EmailPreferences Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_email_preferences]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The email address of this user. If you want to get the user information
    // Of the user making the Content API request, you can also use "me" instead
    // Of an email address.
    // String email = "testUser@gmail.com";
    String email = "me";

    updateEmailPreferences(config, email);
  }
}
