package shopping.common;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.io.File;
import java.io.IOException;

/**
 * Base class for configuration objects handled by DataStores for the two APIs.
 *
 * <p>The Authenticator and ConfigDataStoreFactory classes need access to the emailAddress used for
 * authentication. The ConfigDataStoreFactory class also needs access to the token and the ability
 * to save the configuration.
 */
public abstract class Config extends GenericJson {
  @Key private String emailAddress;

  @Key private Token token;

  // The path where the configuration can be found. This is a meta-property
  // of the configuration, not one stored in it, but since we store the
  // authentication credentials in the same place, the Authenticator can use
  // this to determine where to look for those, and storing this here avoids
  // having to pass around the path to the data store, which calls save().
  private File path;

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

  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }

  public abstract void save() throws IOException;
}
