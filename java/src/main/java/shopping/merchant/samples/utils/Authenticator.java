// Copyright 2023 Google LLC
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

package shopping.merchant.samples.utils;

import com.google.auth.oauth2.GoogleCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that contains all the authentication logic. Currently only supports service account
 * credentials.
 */
public class Authenticator {

  public GoogleCredentials authenticate() throws IOException {
    Config config = Config.load();
    File serviceAccountFile = new File(config.getPath(), "service-account.json");
    System.out.printf("Checking for service account file at: %s%n", serviceAccountFile);
    if (serviceAccountFile.exists()) {
      System.out.println("Attempting to load service account credentials");
      try (InputStream inputStream = new FileInputStream(serviceAccountFile)) {
        GoogleCredentials credential = GoogleCredentials.fromStream(inputStream);
        System.out.println("Successfully loaded service account credentials");
        return credential;
      }
    }

    throw new FileNotFoundException(
        "No service account file was found, so no authentication credentials could be created.");
  }
}
