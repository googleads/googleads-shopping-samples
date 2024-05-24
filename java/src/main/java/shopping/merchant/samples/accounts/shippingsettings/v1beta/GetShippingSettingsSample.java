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

package shopping.merchant.samples.accounts.shippingsettings.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.GetShippingSettingsRequest;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettings;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettingsName;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettingsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettingsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the ShippingSettings for a given Merchant Center account. */
public class GetShippingSettingsSample {

  // [START get_shippingsettings]
  public static void getShippingSettings(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ShippingSettingsServiceSettings shippingSettingsServiceSettings =
        ShippingSettingsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates ShippingSettings name to identify ShippingSettings.
    String name =
        ShippingSettingsName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (ShippingSettingsServiceClient shippingSettingsServiceClient =
        ShippingSettingsServiceClient.create(shippingSettingsServiceSettings)) {

      // The name has the format: accounts/{account}/shippingSettings
      GetShippingSettingsRequest request =
          GetShippingSettingsRequest.newBuilder().setName(name).build();

      System.out.println("Sending Get ShippingSettings request:");
      ShippingSettings response = shippingSettingsServiceClient.getShippingSettings(request);

      System.out.println("Retrieved ShippingSettings below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_shipping_settings]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getShippingSettings(config);
  }
}
