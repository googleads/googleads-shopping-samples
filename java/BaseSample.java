import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.content.ShoppingContent;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Base class for the API samples.
 */
public abstract class BaseSample {
  protected BigInteger merchantId;
  protected ShoppingContent content;

  private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
  private final Credential credential;
  private final HttpTransport httpTransport;
  private final Config config;

  public BaseSample() {
    config = loadConfig();
    merchantId = config.getMerchantId();
    httpTransport = createHttpTransport();
    credential = createCredential();
    credential.setRefreshToken(config.getRefreshToken());
    content = createContentService();
  }

  protected Config loadConfig() {
    try {
      return Config.load();
    } catch (IOException e) {
      System.out.println("There was an error while loading configuration.");
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  protected HttpTransport createHttpTransport() {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  protected ShoppingContent createContentService() {
    return new ShoppingContent.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(config.getApplicationName())
        .build();
  }

  protected Credential createCredential() {
    return new GoogleCredential.Builder()
        .setClientSecrets(config.getClientId(), config.getClientSecret())
        .setJsonFactory(jsonFactory)
        .setTransport(httpTransport)
        .build();
  }

  public abstract void execute() throws IOException;
}
