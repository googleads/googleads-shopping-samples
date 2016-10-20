package shopping.v2.samples;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.content.ShoppingContent;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

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
  private final Authenticator authenticator;

  public BaseSample() throws IOException {
    httpTransport = createHttpTransport();
    authenticator = loadAuthentication();
    credential = createCredential();
    config = loadConfig();
    merchantId = config.getMerchantId();
    content = createContentService();
  }

  protected Config loadConfig() throws IOException {
    return Config.load();
  }

  protected HttpTransport createHttpTransport() throws IOException {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
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

  protected Credential createCredential() throws IOException {
    return authenticator.authenticate();
  }

  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory);
  }

  public abstract void execute() throws IOException;
}
