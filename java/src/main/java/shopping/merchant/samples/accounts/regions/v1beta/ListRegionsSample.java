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
import com.google.shopping.merchant.accounts.v1beta.ListRegionsRequest;
import com.google.shopping.merchant.accounts.v1beta.Region;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceClient;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceClient.ListRegionsPagedResponse;
import com.google.shopping.merchant.accounts.v1beta.RegionsServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to list all the regions for a given Merchant Center account. */
public class ListRegionsSample {

  private static String getParent(String accountId) {
    return String.format("accounts/%s", accountId);
  }

  // [START list_regions]
  public static void listRegions(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    RegionsServiceSettings regionsServiceSettings =
        RegionsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates parent to identify the account from which to list all regions.
    String parent = getParent(config.getAccountId().toString());

    // Calls the API and catches and prints any network failures/errors.
    try (RegionsServiceClient regionsServiceClient =
        RegionsServiceClient.create(regionsServiceSettings)) {

      // The parent has the format: accounts/{account}
      ListRegionsRequest request = ListRegionsRequest.newBuilder().setParent(parent).build();

      System.out.println("Sending list regions request:");
      ListRegionsPagedResponse response = regionsServiceClient.listRegions(request);

      int count = 0;

      // Iterates over all rows in all pages and prints the region
      // in each row.
      // `response.iterateAll()` automatically uses the `nextPageToken` and recalls the
      // request to fetch all pages of data.
      for (Region element : response.iterateAll()) {
        System.out.println(element);
        count++;
      }
      System.out.print("The following count of elements were returned: ");
      System.out.println(count);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END list_regions]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    listRegions(config);
  }
}
