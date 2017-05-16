package shopping.content.v2.samples;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.model.AccountIdentifier;
import com.google.api.services.content.model.AccountsAuthInfoResponse;
import com.google.api.services.content.model.Error;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import shopping.common.Authenticator;
import shopping.common.BaseSample;

/** Base class for the Content API samples. */
public abstract class ContentSample extends BaseSample {
  protected ContentConfig config;
  protected ShoppingContent content;
  protected ShoppingContent sandbox;

  public ContentSample(String args[]) throws IOException {
    super(args);
    ShoppingContent.Builder builder =
        new ShoppingContent.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(config.getApplicationName());
    content = createService(builder);
    sandbox = createSandboxContentService(builder);
    config.setIsMCA(retrieveMCAStatus());
  }

  protected void loadConfig(File path) throws IOException {
    config = ContentConfig.load(path);
  }

  // This method assumes a builder that's already been used to create the standard
  // ShoppingContent service object, so we'll just modify the path if needed.
  protected ShoppingContent createSandboxContentService(ShoppingContent.Builder builder) {
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

  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory, ShoppingContentScopes.all(), config);
  }

  protected boolean retrieveMCAStatus() throws IOException {
    System.out.printf("Retrieving MCA status for account %d.%n", config.getMerchantId());
    AccountsAuthInfoResponse resp = content.accounts().authinfo().execute();
    for (AccountIdentifier ids : resp.getAccountIdentifiers()) {
      if (ids.getAggregatorId() == config.getMerchantId()) {
        return true;
      }
      if (ids.getMerchantId() == config.getMerchantId()) {
        return false;
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
    return false;
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
}
