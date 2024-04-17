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

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.JsonToken;
import com.google.api.client.json.gson.GsonFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Wrapper for the JSON configuration file used to keep user specific details like the Merchant
 * Center account ID.
 */
public class Config {
  private static final File BASE_PATH =
      new File(System.getProperty("user.home"), "shopping-samples");
  private static final String CONTENT_DIR = "content";
  private static final String FILE_NAME = "merchant-info.json";

  private BigInteger accountId;

  private String accountSampleUser;

  private BigInteger accountSampleAdsCID;

  private File path;

  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }

  public static Config load() throws IOException {
    File configPath = new File(BASE_PATH, CONTENT_DIR);
    if (!configPath.exists()) {
      throw new FileNotFoundException(
          "Content API for Shopping configuration directory '"
              + configPath.getCanonicalPath()
              + "' does not exist");
    }
    File configFile = new File(configPath, FILE_NAME);
    try (InputStream inputStream = new FileInputStream(configFile)) {
      Config config = new Config();
      JsonParser jParser = new GsonFactory().createJsonParser(inputStream);
      while (jParser.nextToken() != JsonToken.END_OBJECT) {
        String fieldname = jParser.getCurrentName();
        if ("merchantId".equals(fieldname)) {
          jParser.nextToken();
          config.setAccountId(new BigInteger(jParser.getText()));
        }
        if ("accountSampleUser".equals(fieldname)) {
          jParser.nextToken();
          config.setAccountSampleUser(jParser.getText());
        }
        if ("accountSampleAdWordsCID".equals(fieldname)) {
          jParser.nextToken();
          config.setAccountSampleAdsCID(new BigInteger(jParser.getText()));
        }
      }
      jParser.close();
      config.setPath(configPath);
      return config;
    } catch (IOException e) {
      throw new IOException(
          "Could not find or read the config file at "
              + configFile.getCanonicalPath()
              + ". You can use the "
              + FILE_NAME
              + " file in the "
              + "samples root as a template.");
    }
  }

  public BigInteger getAccountId() {
    return accountId;
  }

  public void setAccountId(BigInteger accountId) {
    this.accountId = accountId;
  }

  public String getAccountSampleUser() {
    return accountSampleUser;
  }

  public void setAccountSampleUser(String accountSampleUser) {
    this.accountSampleUser = accountSampleUser;
  }

  public BigInteger getAccountSampleAdsCID() {
    return accountSampleAdsCID;
  }

  public void setAccountSampleAdsCID(BigInteger accountSampleAdsCID) {
    this.accountSampleAdsCID = accountSampleAdsCID;
  }

  public String getMerchantURL() {
    return ("accounts/" + this.accountId + "/");
  }
}
