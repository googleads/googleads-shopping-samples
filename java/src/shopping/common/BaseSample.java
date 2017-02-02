package shopping.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Base class for both sets of API samples.
 */
public abstract class BaseSample {
  protected final Credential credential;
  protected final HttpTransport httpTransport;
  protected final Authenticator authenticator;
  protected final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  public BaseSample() throws IOException {
    httpTransport = createHttpTransport();
    loadConfig();
    authenticator = loadAuthentication();
    credential = createCredential();
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
    GoogleJsonError err = e.getDetails();
    // err can be null if response is not JSON
    if (err != null) {
      // For errors in the 4xx range, print out the errors and continue normally.
     if (err.getCode() >= 400 && err.getCode() < 500) {
       System.out.printf("There are %d error(s)%n", err.getErrors().size());
       for (ErrorInfo info : err.getErrors()) {
         System.out.printf("- [%s] %s%n", info.getReason(), info.getMessage());
        }
      } else {
        throw e;
      }
    } else {
      throw e;
    }
  }

  protected abstract void loadConfig() throws IOException;
  protected abstract Authenticator loadAuthentication() throws IOException;
  public abstract void execute() throws IOException;
}
