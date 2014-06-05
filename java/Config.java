import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonString;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * Wrapper for the JSON configuration file used to keep user specific details like authentication
 * information and Merchant Center account ID.
 */
public class Config extends GenericJson {
  private static final String FILE_NAME = ".shopping-content-samples.json";
  private static final File CONFIG_FILE = new File(System.getProperty("user.home"), FILE_NAME);

  @Key
  private String clientId = "";

  @Key
  private String clientSecret = "";

  @Key
  @JsonString
  private BigInteger merchantId;

  @Key
  private String applicationName = "";

  @Key
  private String refreshToken = "";

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
          + configFile.getCanonicalPath() + ". You can use the config.json file in the samples "
          + "root as a template.");
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
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

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
