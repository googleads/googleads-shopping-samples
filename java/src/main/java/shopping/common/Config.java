package shopping.common;

import com.google.api.client.json.GenericJson;
import java.io.File;

/**
 * Base class for configuration objects handled by DataStores for the two APIs.
 *
 * <p>The Authenticator and ConfigDataStoreFactory classes need access to the emailAddress used for
 * authentication. The ConfigDataStoreFactory class also needs access to the token and the ability
 * to save the configuration.
 */
public abstract class Config extends GenericJson {
  // The path where the configuration can be found. This is a meta-property
  // of the configuration, not one stored in it, but since we store the
  // authentication credentials in the same place, the Authenticator can use
  // this to determine where to look for those, and storing this here avoids
  // having to pass around the path to the data store, which calls save().
  private File path;

  public File getPath() {
    return path;
  }

  public void setPath(File path) {
    this.path = path;
  }
}
