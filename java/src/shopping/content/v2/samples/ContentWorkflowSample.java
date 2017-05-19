package shopping.content.v2.samples;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.model.AccountIdentifier;
import com.google.api.services.content.model.AccountsAuthInfoResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import shopping.common.Authenticator;
import shopping.common.BaseWorkflowSample;

/** Base class for the Content API workflow samples. */
public abstract class ContentWorkflowSample extends BaseWorkflowSample {
  protected ContentConfig config;
  protected ShoppingContent content;
  protected ShoppingContent sandbox;

  public ContentWorkflowSample(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    this.content = content;
    this.sandbox = sandbox;
    this.config = config;
  }

  protected static ShoppingContent.Builder createStandardBuilder(ContentConfig config)
      throws IOException {
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    HttpTransport httpTransport = null;
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
      System.exit(1);
    }
    Authenticator authenticator =
        new Authenticator(httpTransport, jsonFactory, ShoppingContentScopes.all(), config);
    Credential credential = authenticator.authenticate();

    return new ShoppingContent.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(config.getApplicationName());
  }

  // This method assumes a builder that's already been used to create the standard
  // ShoppingContent service object, so we'll just modify the path if needed.
  protected static ShoppingContent createSandboxContentService(ShoppingContent.Builder builder) {
    try {
      URI u = new URI(builder.getServicePath());
      URI parent = u.resolve("..");
      String lastElem = parent.relativize(u).getPath();
      // If the path ends with "v2/", then use an ending of "v2sandbox/" instead.
      // Otherwise, we'll try and use the same endpoint with a warning.
      if (lastElem.equals("v2/")) {
        builder.setServicePath(parent.resolve("v2sandbox/").getPath());
      } else {
        System.out.println("Using same API endpoint for standard and sandbox service calls.");
        System.out.println("Order samples will fail if sandbox methods not supported.");
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    return builder.build();
  }

  protected static void retrieveMCAStatus(ShoppingContent content, ContentConfig config)
      throws IOException {
    System.out.printf("Retrieving MCA status for account %d.%n", config.getMerchantId());
    AccountsAuthInfoResponse resp = content.accounts().authinfo().execute();
    for (AccountIdentifier ids : resp.getAccountIdentifiers()) {
      if (ids.getAggregatorId() == config.getMerchantId()) {
        config.setIsMCA(true);
        return;
      }
      if (ids.getMerchantId() == config.getMerchantId()) {
        config.setIsMCA(false);
        return;
      }
    }
    // If we are not explicitly authenticated as a user of the configured account,
    // then it should be a subaccount of an MCA we are authenticated for. Check to
    // see if we have access by calling Accounts.get().
    try {
      content.accounts().get(config.getMerchantId(), config.getMerchantId()).execute();
    } catch (GoogleJsonResponseException e) {
      throw new IllegalArgumentException(
          String.format(
              "Authenticated user does not have access to account %d", config.getMerchantId()),
          e);
    }
    // Sub-accounts cannot be MCAs.
    config.setIsMCA(false);
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
}
