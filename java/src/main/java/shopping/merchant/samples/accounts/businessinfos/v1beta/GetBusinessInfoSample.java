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

package shopping.merchant.samples.accounts.businessinfos.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfo;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoName;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoServiceClient;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.GetBusinessInfoRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the business info of a Merchant Center account. */
public class GetBusinessInfoSample {

  // [START get_business_info]
  public static void getBusinessInfo(Config config) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    BusinessInfoServiceSettings businessInfoServiceSettings =
        BusinessInfoServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates BusinessInfo name to identify the BusinessInfo.
    String name =
        BusinessInfoName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (BusinessInfoServiceClient businessInfoServiceClient =
        BusinessInfoServiceClient.create(businessInfoServiceSettings)) {

      // The name has the format: accounts/{account}/businessInfo
      GetBusinessInfoRequest request = GetBusinessInfoRequest.newBuilder().setName(name).build();

      System.out.println("Sending get BusinessInfo request:");
      BusinessInfo response = businessInfoServiceClient.getBusinessInfo(request);

      System.out.println("Retrieved BusinessInfo below");
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_business_info]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    getBusinessInfo(config);
  }
}
