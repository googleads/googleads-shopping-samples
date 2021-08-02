package shopping.content.v2_1.samples.liasettings;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import shopping.content.v2_1.samples.ContentConfig;

/**
 * Wrapper for the JSON configuration file used to keep LIA user specific details like the GMB
 * email, etc.
 */
public class LiaConfig extends ContentConfig {
  private static final String CONTENT_DIR = "content";
  private static final String FILE_NAME = "merchant-info.json";

  @Key private boolean createSubAccount;

  @Key private String subAccountConfigPath;

  // ID of the newly created account. Should not be in the config json but filled by the
  // LiaAccountWorkflow.
  @Key private BigInteger accountId;

  @Key private String gmbEmail;

  public static LiaConfig load(File basePath) throws IOException {
    if (basePath == null) {
      return new LiaConfig();
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
      LiaConfig config = new JacksonFactory().fromInputStream(inputStream, LiaConfig.class);
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

  public boolean getCreateSubAccount() {
    return createSubAccount;
  }

  public void setCreateSubAccount(boolean createSubAccount) {
    this.createSubAccount = createSubAccount;
  }

  public String getSubAccountConfigPath() {
    return subAccountConfigPath;
  }

  public void setSubAccountConfigPath(String subAccountConfigPath) {
    this.subAccountConfigPath = subAccountConfigPath;
  }

  public BigInteger getAccountId() {
    return accountId;
  }

  public void setAccountId(BigInteger accountId) {
    this.accountId = accountId;
  }

  public String getGmbEmail() {
    return gmbEmail;
  }

  public void setGmbEmail(String gmbEmail) {
    this.gmbEmail = gmbEmail;
  }
}
