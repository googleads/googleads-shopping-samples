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
  protected static final File CONTENT_DIR = new File(CONFIG_DIR, "content");
  private static final String FILE_NAME = "merchant-info.json";
  private static final File CONFIG_FILE = new File(CONTENT_DIR, FILE_NAME);

  @Key
  private BigInteger merchantId;

  @Key
  private String applicationName;

  @Key
  private String websiteUrl;

  @Key
  private String accountSampleUser;

  @Key
  private BigInteger accountSampleAdWordsCID;

  @Key
  private boolean isMCA;

  public static ContentConfig load() throws IOException {
    return ContentConfig.load(CONFIG_FILE);
  }

  public static ContentConfig load(File configFile) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(configFile);
      return new JacksonFactory().fromInputStream(inputStream, ContentConfig.class);
    } catch (IOException e) {
      throw new IOException("Could not find or read the config file at "
          + configFile.getCanonicalPath() + ". You can use the " + FILE_NAME + " file in the "
          + "samples root as a template.");
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  public void save() throws IOException {
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(ContentConfig.CONFIG_FILE);
      JsonGenerator generator = new JacksonFactory().createJsonGenerator(outputStream,
          Charset.defaultCharset());
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
