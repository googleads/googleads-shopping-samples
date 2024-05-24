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
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.accounts.v1beta.Homepage;
import com.google.shopping.merchant.accounts.v1beta.HomepageName;
import com.google.shopping.merchant.accounts.v1beta.HomepageServiceClient;
import com.google.shopping.merchant.accounts.v1beta.HomepageServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.UpdateHomepageRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update a homepage to a new URL. */
public class UpdateHomepageSample {

  // [START update_homepage]
  public static void updateHomepage(Config config, String uri) throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    HomepageServiceSettings homepageServiceSettings =
        HomepageServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates homepage name to identify homepage.
    String name =
        HomepageName.newBuilder().setAccount(config.getAccountId().toString()).build().toString();

    // Create a homepage with the updated fields.
    Homepage homepage = Homepage.newBuilder().setName(name).setUri(uri).build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("uri").build();

    try (HomepageServiceClient homepageServiceClient =
        HomepageServiceClient.create(homepageServiceSettings)) {

      UpdateHomepageRequest request =
          UpdateHomepageRequest.newBuilder().setHomepage(homepage).setUpdateMask(fieldMask).build();

      System.out.println("Sending Update Homepage request");
      Homepage response = homepageServiceClient.updateHomepage(request);
      System.out.println("Updated Homepage Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_homepage]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    // The URI (a URL) of the store's homepage.
    String uri = "https://example.com";

    updateHomepage(config, uri);
  }
}
