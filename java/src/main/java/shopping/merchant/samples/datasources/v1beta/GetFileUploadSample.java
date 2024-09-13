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

package shopping.merchant.samples.datasources.v1beta;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.shopping.merchant.datasources.v1beta.FileUpload;
import com.google.shopping.merchant.datasources.v1beta.FileUploadName;
import com.google.shopping.merchant.datasources.v1beta.FileUploadsServiceClient;
import com.google.shopping.merchant.datasources.v1beta.FileUploadsServiceSettings;
import com.google.shopping.merchant.datasources.v1beta.GetFileUploadRequest;
import shopping.merchant.samples.utils.Authenticator;
import shopping.merchant.samples.utils.Config;

/** This class demonstrates how to get the latest data source file upload. */
public class GetFileUploadSample {

  // [START get_file_upload]
  public static void getFileUpload(Config config, String datasourceId) throws Exception {

    // Obtains OAuth token based on the user's configuration.
    GoogleCredentials credential = new Authenticator().authenticate();

    // Creates service settings using the credentials retrieved above.
    FileUploadsServiceSettings fileUploadsServiceSettings =
        FileUploadsServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credential))
            .build();

    // Creates FileUpload name with datasource id to identify datasource.
    String name =
        FileUploadName.newBuilder()
            .setAccount(config.getAccountId().toString())
            .setDatasource(datasourceId)
            .setFileupload("latest")
            .build()
            .toString();

    // Calls the API and catches and prints any network failures/errors.
    try (FileUploadsServiceClient fileUploadsServiceClient =
        FileUploadsServiceClient.create(fileUploadsServiceSettings)) {

      // The name has the format: accounts/{account}/datasources/{datasource}/fileUploads/latest
      GetFileUploadRequest request = GetFileUploadRequest.newBuilder().setName(name).build();

      System.out.println("Sending get FileUpload request:");
      FileUpload response = fileUploadsServiceClient.getFileUpload(request);

      System.out.println("Retrieved FileUpload below");
      System.out.println(response);
      //   return response;
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  // [END get_file_upload]

  public static void main(String[] args) throws Exception {
    Config config = Config.load();
    // An ID assigned to a datasource by Google.
    String datasourceId = "123456789";

    getFileUpload(config, datasourceId);
  }
}
