package shopping.content.v2_1.samples;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import shopping.common.Config;

/**
 * Wrapper for the JSON configuration file used to keep user specific details like the Merchant
 * Center account ID.
 */
public class ContentConfig extends Config {
  private static final String CONTENT_DIR = "content";
  private static final String FILE_NAME = "merchant-info.json";

  @Key private BigInteger merchantId;

  @Key private String accountSampleUser;

  @Key private BigInteger accountSampleAdWordsCID;

  // These are no longer set via configuration, but instead by querying the API.
  private boolean isMCA;
  private String websiteUrl;

  public static ContentConfig load(File basePath) throws IOException {
    if (basePath == null) {
      return new ContentConfig();
    }
    File configPath = new File(basePath, CONTENT_DIR);
    if (!configPath.exists()) {
      throw new IOException(
          "Content API for Shopping configuration directory '"
              + configPath.getCanonicalPath()
              + "' does not exist");
    }
    File configFile = new File(configPath, FILE_NAME);
    try (InputStream inputStream = new FileInputStream(configFile)) {
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
    }
  }

  public BigInteger getMerchantId() {
    return merchantId;
  }

  public void setMerchantId(BigInteger merchantId) {
    this.merchantId = merchantId;
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
