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

package shopping.merchant.samples.accounts.homepages.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.GetHomepageRequest;
import com.google.shopping.merchant.accounts.v1beta.Homepage;
import com.google.shopping.merchant.accounts.v1beta.HomepageName;
import com.google.shopping.merchant.accounts.v1beta.HomepageServiceClient;
import com.google.shopping.merchant.accounts.v1beta.HomepageServiceSettings;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the homepage for a given Merchant Center account */
public class GetHomepageSample {

  // [START get_homepage]
  public static void getHomepage(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    HomepageServiceSettings homepageServiceSettings =
        HomepageServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates Homepage name to identify Homepage.
    String name =
        HomepageName.newBuilder().setAccount(config.getAccountId().toString()).build().toString();

    // Calls the API and catches and prints any network failures/errors.
    try (HomepageServiceClient homepageServiceClient =
        HomepageServiceClient.create(homepageServiceSettings)) {

      // The name has the format: accounts/{account}/homepage
      GetHomepageRequest request = GetHomepageRequest.newBuilder().setName(name).build();

      System.out.println("Sending Get Homepage request:");
      Homepage response = homepageServiceClient.getHomepage(request);

      System.out.println("Retrieved Homepage below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_homepage]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getHomepage(config);
  }
}
