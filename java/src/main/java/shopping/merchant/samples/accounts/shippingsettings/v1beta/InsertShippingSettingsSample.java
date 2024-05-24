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
import com.google.shopping.merchant.accounts.v1beta.DeliveryTime;
import com.google.shopping.merchant.accounts.v1beta.InsertShippingSettingsRequest;
import com.google.shopping.merchant.accounts.v1beta.RateGroup;
import com.google.shopping.merchant.accounts.v1beta.Service;
import com.google.shopping.merchant.accounts.v1beta.Service.ShipmentType;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettings;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettingsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.ShippingSettingsServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.Value;
import com.google.shopping.type.Price;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to insert a ShippingSettings for a Merchant Center account. */
public class InsertShippingSettingsSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START insert_shippingsettings]
  public static void insertShippingSettings(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    ShippingSettingsServiceSettings shippingSettingsServiceSettings =
        ShippingSettingsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to insert the shippingsettings.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (ShippingSettingsServiceClient shippingSettingsServiceClient =
        ShippingSettingsServiceClient.create(shippingSettingsServiceSettings)) {

      InsertShippingSettingsRequest request =
          InsertShippingSettingsRequest.newBuilder()
              .setParent(parent)
              .setShippingSetting(
                  ShippingSettings.newBuilder()
                      // Etag needs to be an empty string on initial insert
                      // On future inserts, call GET first to get the Etag
                      // Then use the retrieved Etag on future inserts.
                      // NOTE THAT ON THE INITIAL INSERT, YOUR SHIPPING SETTINGS WILL
                      // NOT BE STORED, YOU HAVE TO CALL INSERT AGAIN WITH YOUR
                      // RETRIEVED ETAG.
                      // .setEtag("")
                      .setEtag("PPa=")
                      .addServices(
                          Service.newBuilder()
                              .setServiceName("Canadian Postal Service")
                              .setActive(true)
                              .addDeliveryCountries("CA")
                              .setCurrencyCode("CAD")
                              .setDeliveryTime(
                                  DeliveryTime.newBuilder()
                                      .setMinTransitDays(0)
                                      .setMaxTransitDays(3)
                                      .setMinHandlingDays(0)
                                      .setMaxHandlingDays(3)
                                      .build())
                              .addRateGroups(
                                  RateGroup.newBuilder()
                                      .addApplicableShippingLabels("Oversized")
                                      .addApplicableShippingLabels("Perishable")
                                      .setSingleValue(Value.newBuilder().setPricePercentage("5.4"))
                                      .setName("Oversized and Perishable items")
                                      .build())
                              .setShipmentType(ShipmentType.DELIVERY)
                              .setMinimumOrderValue(
                                  Price.newBuilder()
                                      .setAmountMicros(10000000)
                                      .setCurrencyCode("CAD")
                                      .build())
                              .build())
                      .build())
              .build();

      System.out.println("Sending insert ShippingSettings request");
      ShippingSettings response = shippingSettingsServiceClient.insertShippingSettings(request);
      System.out.println("Inserted ShippingSettings Name below");
      System.out.println(response.getName());
      // You can apply ShippingSettings to specific products by using the `shippingLabel` field
      // on the product.
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END insert_shippingsettings]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    insertShippingSettings(config);
  }
}
