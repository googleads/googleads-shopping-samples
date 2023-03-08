package shopping.common;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.commons.cli.CommandLine;

/** Base class for both sets of API samples. */
public abstract class BaseSample {
  protected static final String ENDPOINT_ENV_VAR = "GOOGLE_SHOPPING_SAMPLES_ENDPOINT";

  protected final HttpRequestInitializer initializer;
  protected final HttpTransport httpTransport;
  protected final Authenticator authenticator;
  protected final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
  protected final CommandLine parsedArgs;

  public BaseSample(String[] args) throws IOException {
    parsedArgs = BaseOption.parseOptions(args);
    if (NO_CONFIG.isSet(parsedArgs)) {
      loadConfig(null);
    } else {
      loadConfig(BaseOption.checkedConfigPath(parsedArgs));
    }
    httpTransport = createHttpTransport();
    authenticator = loadAuthentication();
    // Chaining HttpRequestInitializers together to increase timeout duration and implement logging
    initializer = BaseOption.increaseTimeout(
        BaseOption.installLogging(createCredential(), parsedArgs));
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

  protected Credential createCredential() throws IOException {
    return authenticator.authenticate();
  }

  protected void checkGoogleJsonResponseException(GoogleJsonResponseException e)
      throws GoogleJsonResponseException {
    BaseWorkflowSample.checkGoogleJsonResponseException(e);
  }

  protected <T extends AbstractGoogleClient> T createService(AbstractGoogleClient.Builder builder) {
    return BaseWorkflowSample.createService(builder);
  }

  protected abstract void loadConfig(File configPath) throws IOException;

  protected abstract Authenticator loadAuthentication() throws IOException;

  public abstract void execute() throws IOException;
}
