package shopping.manufacturers.v1.samples;

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
 * Wrapper for the JSON configuration file used to keep user specific details like the Manufacturer
 * Center account ID.
 */
public class ManufacturersConfig extends Config {
  protected static final File MANUFACTURERS_DIR = new File(CONFIG_DIR, "manufacturers");
  private static final String FILE_NAME = "manufacturer-info.json";
  private static final File CONFIG_FILE = new File(MANUFACTURERS_DIR, FILE_NAME);

  @Key
  private BigInteger manufacturerId;

  @Key
  private String applicationName;

  @Key
  private String websiteUrl;

  public static ManufacturersConfig load() throws IOException {
    return ManufacturersConfig.load(CONFIG_FILE);
  }

  public static ManufacturersConfig load(File configFile) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(configFile);
      return new JacksonFactory().fromInputStream(inputStream, ManufacturersConfig.class);
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

  @Override
  public void save() throws IOException {
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(ManufacturersConfig.CONFIG_FILE);
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

  public BigInteger getManufacturerId() {
    return manufacturerId;
  }

  public void setManufacturerId(BigInteger manufacturerId) {
    this.manufacturerId = manufacturerId;
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
}
