package shopping.v2.samples;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.content.ShoppingContent;

import com.google.api.services.content.model.Error;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Base class for the API samples.
 */
public abstract class BaseSample {
  protected ShoppingContent content;
  protected ShoppingContent sandbox;

  private final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
  private final Credential credential;
  private final HttpTransport httpTransport;
  protected final Config config;
  private final Authenticator authenticator;

  public BaseSample() throws IOException {
    httpTransport = createHttpTransport();
    config = loadConfig();
    authenticator = loadAuthentication();
    credential = createCredential();
    content = createContentService();
    sandbox = createSandboxContentService();
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

  protected ShoppingContent createSandboxContentService() {
    ShoppingContent.Builder builder =
        new ShoppingContent.Builder(httpTransport, jsonFactory, credential);
    return builder.setApplicationName(config.getApplicationName())
        .setServicePath("content/v2sandbox/")
        .build();
  }

  protected Credential createCredential() throws IOException {
    return authenticator.authenticate();
  }

  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory, config);
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

  protected void checkMCA() {
    if (!config.getIsMCA()) {
      throw new IllegalStateException(
          "Sample requires the authenticating account to be a multi-client account");
    }
  }

  protected void checkNonMCA() {
    if (config.getIsMCA()) {
      throw new IllegalStateException(
          "Sample requires the authenticating account to be a non-multi-client account");
    }
  }

  protected void printWarnings(List<Error> warnings) {
    printWarnings(warnings, "");
  }

  protected void printWarnings(List<Error> warnings, String prefix) {
    printErrors(warnings, prefix, "warning");
  }

  protected void printErrors(List<Error> errors) {
    printErrors(errors, "");
  }

  protected void printErrors(List<Error> errors, String prefix) {
    printErrors(errors, prefix, "error");
  }

  protected void printErrors(List<Error> errors, String prefix, String type) {
    if (errors == null) {
      return;
    }
    System.out.printf(prefix + "There are %d %s(s):%n", errors.size(), type);
    for (Error err : errors) {
      System.out.printf(prefix + "- [%s] %s%n", err.getReason(), err.getMessage());
    }
  }

  public abstract void execute() throws IOException;
}
