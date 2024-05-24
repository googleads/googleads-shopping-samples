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
import com.google.shopping.merchant.accounts.v1beta.EmailPreferences;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesName;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesServiceClient;
import com.google.shopping.merchant.accounts.v1beta.EmailPreferencesServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.GetEmailPreferencesRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the email preferences of a Merchant Center account. */
public class GetEmailPreferencesSample {

  // [START get_email_preferences]
  public static void getEmailPreferences(Config config, String email) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    EmailPreferencesServiceSettings emailPreferencesServiceSettings =
        EmailPreferencesServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates EmailPreferences name to identify the EmailPreferences.
    String name =
        EmailPreferencesName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setEmail(email)
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (EmailPreferencesServiceClient emailPreferencesServiceClient =
        EmailPreferencesServiceClient.create(emailPreferencesServiceSettings)) {

      // The name has the format: accounts/{account}/users/{user}/emailPreferences
      GetEmailPreferencesRequest request =
          GetEmailPreferencesRequest.newBuilder().setName(name).build();

      System.out.println("Sending get EmailPreferences request:");
      EmailPreferences response = emailPreferencesServiceClient.getEmailPreferences(request);

      System.out.println("Retrieved EmailPreferences below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_email_preferences]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The email address of this user. If you want to get the user information
    // Of the user making the Content API request, you can also use "me" instead
    // Of an email address.
    String email = "testUser@gmail.com";
    // String email = "me";

    getEmailPreferences(config, email);
  }
}
