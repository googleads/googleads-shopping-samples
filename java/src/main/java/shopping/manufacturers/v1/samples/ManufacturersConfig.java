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
  private static final String MANUFACTURERS_DIR = "manufacturers";
  private static final String FILE_NAME = "manufacturer-info.json";

  @Key private BigInteger manufacturerId;

  @Key private String websiteUrl;

  public static ManufacturersConfig load(File basePath) throws IOException {
    if (basePath == null) {
      throw new IllegalArgumentException(
          "Manufacturer Center API samples cannot be run without a configuration directory.");
    }
    InputStream inputStream = null;
    File configPath = new File(basePath, MANUFACTURERS_DIR);
    if (!configPath.exists()) {
      throw new IOException(
          "Manufacturer Center API configuration directory '"
              + configPath.getCanonicalPath()
              + "' does not exist");
    }
    File configFile = new File(configPath, FILE_NAME);
    try {
      inputStream = new FileInputStream(configFile);
      ManufacturersConfig config =
          new JacksonFactory().fromInputStream(inputStream, ManufacturersConfig.class);
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

  @Override
  public void save() throws IOException {
    File configFile = new File(getPath(), FILE_NAME);
    OutputStream outputStream = null;
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

  public BigInteger getManufacturerId() {
    return manufacturerId;
  }

  public void setManufacturerId(BigInteger manufacturerId) {
    this.manufacturerId = manufacturerId;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }
}
