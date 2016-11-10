package shopping.v2.samples;

import com.google.api.client.json.GenericJson;
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

/**
 * Wrapper for the JSON configuration file used to keep user specific details like the Merchant
 * Center account ID.
 */
public class Config extends GenericJson {
  protected static final File CONFIG_DIR =
      new File(System.getProperty("user.home"), ".shopping-content-samples/");

  private static final String FILE_NAME = "merchant-info.json";
  private static final File CONFIG_FILE = new File(CONFIG_DIR, FILE_NAME);

  @Key
  private BigInteger merchantId;

  @Key
  private String applicationName;

  @Key
  private String emailAddress;

  @Key
  private String websiteUrl;

  @Key
  private String accountSampleUser;

  @Key
  private BigInteger accountSampleAdWordsCID;

  @Key
  private boolean isMCA;

  @Key
  private Token token;

  public static Config load() throws IOException {
    return Config.load(CONFIG_FILE);
  }

  public static Config load(File configFile) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(configFile);
      return new JacksonFactory().fromInputStream(inputStream, Config.class);
    } catch (IOException e) {
      throw new IOException("Could not find or read the config file at "
          + configFile.getCanonicalPath() + ". You can use the merchant-info.json file in the "
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
      outputStream = new FileOutputStream(Config.CONFIG_FILE);
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

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
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

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }
}
