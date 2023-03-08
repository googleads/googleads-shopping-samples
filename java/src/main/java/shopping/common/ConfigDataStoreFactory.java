package shopping.common;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of DataStoreFactory backed by a file in the samples configuration directory. We
 * only store a single token, and to ensure that real user IDs aren't floating around, we only
 * operate on tokens stored using the {@link UNUSED_ID} constant.
 *
 * <p>Since we only plan to hand this type of {@link DataStoreFactory} over to a {@link
 * com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow} object in the {@link
 * Authenticator} class, we make the inner class that extends {@link DataStore} specific to the
 * {@link StoredCredential} class, and cast it to the expected generic type in the {@link
 * #getDataStore} method.
 */
public class ConfigDataStoreFactory implements DataStoreFactory {
  public static final String UNUSED_ID = "unused";

  private Config config;

  public ConfigDataStoreFactory(Config config) {
    this.config = config;

  }

  @SuppressWarnings("unchecked")
  public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
    return (DataStore<V>) new ConfigDataStore(id, config);
  }

  private class ConfigDataStore extends AbstractDataStore<StoredCredential>
      implements DataStore<StoredCredential> {
    private static final String OAUTH_TOKEN_FILENAME = "stored-token.json";

    private final File tokenFile;
    private Token token;

    public ConfigDataStore(String id, Config config) {
      super(ConfigDataStoreFactory.this, id);
      this.tokenFile = new File(config.getPath(), OAUTH_TOKEN_FILENAME);
      try {
        this.token = loadToken();
      } catch (IOException e) {
        this.token = null;
      }
    }

    private Token loadToken() throws IOException {
      try (InputStream inputStream = new FileInputStream(tokenFile)) {
        return new JacksonFactory().fromInputStream(inputStream, Token.class);
      }
    }

    private void writeToken() throws IOException {
      try (OutputStream outputStream = new FileOutputStream(tokenFile);
          OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream)) {
        JsonGenerator generator = new JacksonFactory().createJsonGenerator(outputWriter);
        generator.enablePrettyPrint();
        generator.serialize(token);
        generator.flush();
      }
    }

    private void deleteToken() throws IOException {
      if (token == null) {
        return;
      }
      if (!tokenFile.delete()) {
        throw new IOException("Couldn't delete the token file");
      }
      token = null;
    }

    public int size() throws IOException {
      return token != null ? 1 : 0;
    }

    public Set<String> keySet() throws IOException {
      HashSet<String> hash = new HashSet<String>();
      if (token != null) {
        hash.add(UNUSED_ID);
      }
      return hash;
    }

    public Collection<StoredCredential> values() throws IOException {
      HashSet<StoredCredential> hash = new HashSet<StoredCredential>();
      if (token != null) {
        hash.add(get(UNUSED_ID));
      }
      return hash;
    }

    public StoredCredential get(String key) throws IOException {
      if (key.equals(UNUSED_ID) && token != null) {
        return token.toStoredCredential();
      }
      return null;
    }

    public DataStore<StoredCredential> set(String key, StoredCredential value) throws IOException {
      if (key != UNUSED_ID) {
        throw new IOException("Unexpected real user ID");
      }
      token = Token.fromStoredCredential(value);
      writeToken();
      return this;
    }

    public DataStore<StoredCredential> clear() throws IOException {
      return delete(UNUSED_ID);
    }

    public DataStore<StoredCredential> delete(String key) throws IOException {
      if (key.equals(UNUSED_ID)) {
        deleteToken();
      }
      return this;
    }
  }
}
