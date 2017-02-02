package shopping.common;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.io.File;
import java.io.IOException;

/**
 * Base class for configuration objects handled by DataStores for the two APIs.
 *
 * The Authenticator and ConfigDataStoreFactory classes need access to the
 * emailAddress used for authentication. The ConfigDataStoreFactory class also
 * needs access to the token and the ability to save the configuration.
 */
public abstract class Config extends GenericJson {
  protected static final File CONFIG_DIR =
      new File(System.getProperty("user.home"), "shopping-samples");

  @Key
  private String emailAddress;

  @Key
  private Token token;

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }

  public abstract void save() throws IOException;
}
