package shopping.content.v2_1.samples;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountIdentifier;
import com.google.api.services.content.model.AccountsAuthInfoResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import org.apache.commons.cli.CommandLine;
import shopping.common.Authenticator;
import shopping.common.BaseOption;
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

  protected static ShoppingContent.Builder createStandardBuilder(
      CommandLine parsedArgs, ContentConfig config) throws IOException {
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

    return new ShoppingContent.Builder(
            httpTransport, jsonFactory, BaseOption.increaseTimeout(
                BaseOption.installLogging(credential, parsedArgs)))
        .setApplicationName("Content API for Shopping Samples");
  }

  // This method assumes a builder that's already been used to create the standard
  // ShoppingContent service object, so we'll just modify the path if needed.
  protected static ShoppingContent createSandboxContentService(ShoppingContent.Builder builder) {
    try {
      URI u = new URI(builder.getServicePath());
      URI parent = u.resolve("..");
      String lastElem = parent.relativize(u).getPath();
      // If the path ends with "v2.1/", then use an ending of "v2.1sandbox/" instead.
      // Otherwise, we'll try and use the same endpoint with a warning.
      if (lastElem.equals("v2.1/")) {
        builder.setServicePath(parent.resolve("v2.1sandbox/").getPath());
      } else {
        System.out.println("Using same API endpoint for standard and sandbox service calls.");
        System.out.println("Order samples will fail if sandbox methods not supported.");
      }
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    return builder.build();
  }

  protected static void retrieveConfiguration(ShoppingContent content, ContentConfig config)
      throws IOException {
    System.out.println("Retrieving information for authenticated account.");
    AccountsAuthInfoResponse resp = content.accounts().authinfo().execute();
    if (resp.getAccountIdentifiers() == null) {
      throw new IllegalArgumentException(
          "Authenticated user has no access to any Merchant Center accounts.");
    }
    if (config.getMerchantId() == null) {
      AccountIdentifier firstAccount = resp.getAccountIdentifiers().get(0);
      if (firstAccount.getMerchantId() == null) {
        config.setMerchantId(firstAccount.getAggregatorId());
      } else {
        config.setMerchantId(firstAccount.getMerchantId());
      }
      System.out.printf("Running samples with Merchant Center %s.%n", config.getMerchantId());
    }
    config.setIsMCA(false);
    for (AccountIdentifier ids : resp.getAccountIdentifiers()) {
      if (ids.getAggregatorId() != null && ids.getAggregatorId().equals(config.getMerchantId())) {
        config.setIsMCA(true);
        break;
      }
      if (ids.getMerchantId() != null && ids.getMerchantId().equals(config.getMerchantId())) {
        break;
      }
    }
    if (config.getIsMCA()) {
      System.out.printf("Merchant Center %s is an MCA.%n", config.getMerchantId());
    } else {
      System.out.printf("Merchant Center %s is not an MCA.%n", config.getMerchantId());
    }
    Account account =
        content.accounts().get(config.getMerchantId(), config.getMerchantId()).execute();
    config.setWebsiteUrl(account.getWebsiteUrl());
    if (config.getWebsiteUrl() == null || config.getWebsiteUrl().equals("")) {
      System.out.printf(
          "Merchant Center %s does not have a configured website.%n", config.getMerchantId());
    } else {
      System.out.printf(
          "Website for Merchant Center %s: %s%n", config.getMerchantId(), config.getWebsiteUrl());
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
}
