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

package shopping.merchant.samples.accounts.regions.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.CreateRegionRequest;
import com.google.shopping.merchant.accounts.v1beta.Region;
import com.google.shopping.merchant.accounts.v1beta.Region.PostalCodeArea;
import com.google.shopping.merchant.accounts.v1beta.Region.PostalCodeArea.PostalCodeRange;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to create a region for a Merchant Center account. */
public class CreateRegionSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START create_region]
  public static void createRegion(Config config, String regionId) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    RegionsServiceSettings regionsServiceSettings =
        RegionsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify where to insert the region.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (RegionsServiceClient regionsServiceClient =
        RegionsServiceClient.create(regionsServiceSettings)) {

      CreateRegionRequest request =
          CreateRegionRequest.newBuilder()
              .setParent(parent)
              .setRegionId(regionId)
              .setRegion(
                  Region.newBuilder()
                      .setDisplayName("New York")
                      .setPostalCodeArea(
                          PostalCodeArea.newBuilder()
                              .setRegionCode("US")
                              .addPostalCodes(
                                  PostalCodeRange.newBuilder()
                                      .setBegin("10001")
                                      .setEnd("10282")
                                      .build())
                              .build())
                      .build())
              .build();

      System.out.println("Sending Create Region request");
      Region response = regionsServiceClient.createRegion(request);
      System.out.println("Inserted Region Name below");
      // The last part of the region name will be the ID of the region.
      // Format: `accounts/{account}/region/{region}`
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END create_region]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // The unique ID of this region.
    String regionId = "123456AB";

    createRegion(config, regionId);
  }
}
