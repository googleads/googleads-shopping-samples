package shopping.v2.samples;

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
 * Wrapper for the JSON configuration file used to keep user specific details like the Merchant
 * Center account ID.
 */
public class Config extends GenericJson {
  protected static final File CONFIG_DIR =
      new File(System.getProperty("user.home"), ".shopping-content-samples/");

  private static final String FILE_NAME = "merchant-info.json";
  private static final File CONFIG_FILE = new File(CONFIG_DIR, FILE_NAME);

  @Key
  @JsonString
  private BigInteger merchantId;

  @Key
  private String applicationName;

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
}
