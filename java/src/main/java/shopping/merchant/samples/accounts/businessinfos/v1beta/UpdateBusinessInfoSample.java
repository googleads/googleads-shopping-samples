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
import com.google.protobuf.FieldMask;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfo;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoName;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoServiceClient;
import com.google.shopping.merchant.accounts.v1beta.BusinessInfoServiceSettings;
import com.google.shopping.merchant.accounts.v1beta.UpdateBusinessInfoRequest;
import com.google.type.PostalAddress;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to update a BusinessInfo to a new address. */
public class UpdateBusinessInfoSample {

  // [START update_businessinfo]
  public static void updateBusinessInfo(Config config) throws Exception {

    GoogleCredentials credential = new Authenticator().authenticate();

    BusinessInfoServiceSettings businessInfoServiceSettings =
        BusinessInfoServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates BusinessInfo name to identify BusinessInfo.
    String name =
        BusinessInfoName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .build()
            .toString();

    // Create a BusinessInfo with the updated fields.
    // Note that you cannot update the RegionCode or the Phone of the business
    BusinessInfo businessInfo =
        BusinessInfo.newBuilder()
            .setName(name)
            .setAddress(
                PostalAddress.newBuilder()
                    .setLanguageCode("en")
                    .setPostalCode("C1107")
                    .addAddressLines(
                        "Av. Alicia Moreau de Justo 350, Cdad. Autónoma de Buenos Aires, Argentina")
                    .build())
            .build();

    FieldMask fieldMask = FieldMask.newBuilder().addPaths("address").build();

    try (BusinessInfoServiceClient businessInfoServiceClient =
        BusinessInfoServiceClient.create(businessInfoServiceSettings)) {

      UpdateBusinessInfoRequest request =
          UpdateBusinessInfoRequest.newBuilder()
              .setBusinessInfo(businessInfo)
              .setUpdateMask(fieldMask)
              .build();

      System.out.println("Sending Update BusinessInfo request");
      BusinessInfo response = businessInfoServiceClient.updateBusinessInfo(request);
      System.out.println("Updated BusinessInfo Name below");
      System.out.println(response.getName());
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END update_businessInfo]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();

    updateBusinessInfo(config);
  }
}
