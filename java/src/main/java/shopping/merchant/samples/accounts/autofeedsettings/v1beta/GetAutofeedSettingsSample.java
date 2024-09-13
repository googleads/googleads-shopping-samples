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

package shopping.merchant.samples.accounts.autofeedsettings.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettings;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsName;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.GetAutofeedSettingsRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the autofeed settings of a Merchant Center account. */
public class GetAutofeedSettingsSample {

  // [START get_autofeed_settings]
  public static void getAutofeedSettings(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    AutofeedSettingsServiceSettings autofeedSettingsServiceSettings =
        AutofeedSettingsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates AutofeedSettings name to identify the AutofeedSettings.
    String name =
        AutofeedSettingsName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (AutofeedSettingsServiceClient autofeedSettingsServiceClient =
        AutofeedSettingsServiceClient.create(autofeedSettingsServiceSettings)) {

      // The name has the format: accounts/{account}/autofeedSettings
      GetAutofeedSettingsRequest request =
          GetAutofeedSettingsRequest.newBuilder().setName(name).build();

      System.out.println("Sending get AutofeedSettings request:");
      AutofeedSettings response = autofeedSettingsServiceClient.getAutofeedSettings(request);

      System.out.println("Retrieved AutofeedSettings below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_autofeed_settings]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getAutofeedSettings(config);
  }
}
