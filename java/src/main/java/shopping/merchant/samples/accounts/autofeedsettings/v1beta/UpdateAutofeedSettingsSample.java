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
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettings;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsName;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.AutofeedSettingsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.UpdateAutofeedSettingsRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update AutofeedSettings to be enabled. */
public class UpdateAutofeedSettingsSample {

  // [START update_autofeedsettings]
  public static void updateAutofeedSettings(Config config) throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    AutofeedSettingsServiceSettings autofeedSettingsServiceSettings =
        AutofeedSettingsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates AutofeedSettings name to identify AutofeedSettings.
    String name =
        AutofeedSettingsName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .build()
            .toString();

    // Create AutofeedSettings with the updated fields.
    AutofeedSettings autofeedSettings =
        AutofeedSettings.newBuilder().setName(name).setEnabled(true).build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("*").build();

    try (AutofeedSettingsServiceClient autofeedSettingsServiceClient =
        AutofeedSettingsServiceClient.create(autofeedSettingsServiceSettings)) {

      UpdateAutofeedSettingsRequest request =
          UpdateAutofeedSettingsRequest.newBuilder()
              .setAutofeedSettings(autofeedSettings)
              .setUpdateMask(fieldMask)
              .build();

      System.out.println("Sending Update AutofeedSettings request");
      AutofeedSettings response = autofeedSettingsServiceClient.updateAutofeedSettings(request);
      System.out.println("Updated AutofeedSettings Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_autofeedsettings]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    updateAutofeedSettings(config);
  }
}
