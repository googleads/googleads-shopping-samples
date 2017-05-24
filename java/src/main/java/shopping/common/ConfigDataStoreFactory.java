package shopping.common;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of DataStoreFactory backed by the samples configuration file.
 *
 * <p>Since we only plan to hand this type of {@link DataStoreFactory} over to a {@link
 * com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow} object in the {@link
 * Authenticator} class, we make the inner class that extends {@link DataStore} specific to the
 * {@link StoredCredential} class, and cast it to the expected generic type in the {@link
 * #getDataStore} method.
 */
public class ConfigDataStoreFactory implements DataStoreFactory {
  private Config config;

  public ConfigDataStoreFactory(Config config) {
    this.config = config;
  }

  @SuppressWarnings("unchecked")
  public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
    return (DataStore<V>) new ConfigDataStore(id, this.config);
  }

  private class ConfigDataStore extends AbstractDataStore<StoredCredential>
      implements DataStore<StoredCredential> {
    private final Config config;

    public ConfigDataStore(String id, Config config) {
      super(ConfigDataStoreFactory.this, id);
      this.config = config;
    }

    public int size() throws IOException {
      return this.config.getToken() == null ? 1 : 0;
    }

    public Set<String> keySet() throws IOException {
      if (this.config.getToken() != null) {
        HashSet<String> hash = new HashSet<String>();
        hash.add(this.config.getEmailAddress());
        return hash;
      } else {
        return new HashSet<String>();
      }
    }

    public Collection<StoredCredential> values() throws IOException {
      if (this.config.getToken() != null) {
        HashSet<StoredCredential> hash = new HashSet<StoredCredential>();
        hash.add(this.config.getToken().toStoredCredential());
        return hash;
      } else {
        return new HashSet<StoredCredential>();
      }
    }

    public StoredCredential get(String key) throws IOException {
      if (key.equals(this.config.getEmailAddress()) && this.config.getToken() != null) {
        return this.config.getToken().toStoredCredential();
      }
      return null;
    }

    public DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
      this.config.setEmailAddress(key);
      this.config.setToken(Token.fromStoredCredential((StoredCredential) value));
      this.config.save();
      return this;
    }

    public DataStore<StoredCredential> clear() throws IOException {
      this.config.setToken(null);
      return this;
    }

    public DataStore<StoredCredential> delete(String key) throws IOException {
      if (key.equals(this.config.getEmailAddress())) {
        this.config.setToken(null);
      }
      return this;
    }
  }
}
