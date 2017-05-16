package shopping.content.v2.samples;

import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import shopping.common.Config;

/**
 * Wrapper for the JSON configuration file used to keep user specific details like the Merchant
 * Center account ID.
 */
public class ContentConfig extends Config {
  private static final String CONTENT_DIR = "content";
  private static final String FILE_NAME = "merchant-info.json";

  @Key private BigInteger merchantId;

  @Key private String applicationName;

  @Key private String websiteUrl;

  @Key private String accountSampleUser;

  @Key private BigInteger accountSampleAdWordsCID;

  // This is no longer done via configuration, but instead by querying the API.
  private boolean isMCA;

  public static ContentConfig load(File basePath) throws IOException {
    InputStream inputStream = null;
    File configPath = new File(basePath, CONTENT_DIR);
    if (!configPath.exists()) {
      throw new IOException(
          "Content API for Shopping configuration directory '"
              + configPath.getCanonicalPath()
              + "' does not exist");
    }
    File configFile = new File(configPath, FILE_NAME);
    try {
      inputStream = new FileInputStream(configFile);
      ContentConfig config = new JacksonFactory().fromInputStream(inputStream, ContentConfig.class);
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
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  public void save() throws IOException {
    OutputStream outputStream = null;
    File configFile = new File(getPath(), FILE_NAME);
    try {
      outputStream = new FileOutputStream(configFile);
      JsonGenerator generator =
          new JacksonFactory().createJsonGenerator(outputStream, Charset.defaultCharset());
      generator.enablePrettyPrint();
      generator.serialize(this);
      generator.flush();
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

  public BigInteger getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(BigInteger merchantId) {
    this.merchantId = merchantId;
  }

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public String getAccountSampleUser() {
    return accountSampleUser;
  }

  public void setAccountSampleUser(String accountSampleUser) {
    this.accountSampleUser = accountSampleUser;
  }

  public BigInteger getAccountSampleAdWordsCID() {
    return accountSampleAdWordsCID;
  }

  public void setAccountSampleAdWordsCID(BigInteger accountSampleAdWordsCID) {
    this.accountSampleAdWordsCID = accountSampleAdWordsCID;
  }

  public boolean getIsMCA() {
    return isMCA;
  }

  public void setIsMCA(boolean isMCA) {
    this.isMCA = isMCA;
  }
}
