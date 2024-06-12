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
import com.google.shopping.merchant.accounts.v1beta.DeleteRegionRequest;
import com.google.shopping.merchant.accounts.v1beta.RegionName;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to delete a given region from a Merchant Center account. */
public class DeleteRegionSample {

  // [START delete_region]
  public static void deleteRegion(Config config, String region) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    RegionsServiceSettings regionsServiceSettings =
        RegionsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Calls the API and catches and prints any network failures/errors.
    try (RegionsServiceClient regionsServiceClient =
        RegionsServiceClient.create(regionsServiceSettings)) {
      DeleteRegionRequest request = DeleteRegionRequest.newBuilder().setName(region).build();

      System.out.println("Sending Delete Region request");
      regionsServiceClient.deleteRegion(request); // no response returned on success
      System.out.println("Delete successful.");
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END delete_region]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    // Creates Region name to identify the Region you want to delete.
    // Format: `accounts/{account}/region/{regionId}`
    String regionId = "{INSERT_REGION_HERE}";

    String region =
        RegionName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setRegion(regionId)
            .build()
            .toString();

    deleteRegion(config, region);
  }
}
