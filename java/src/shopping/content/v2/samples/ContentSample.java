package shopping.content.v2.samples;

import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.model.Error;
import java.io.File;
import java.io.IOException;
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
            .setApplicationName("Content API for Shopping Samples");
    content = createService(builder);
    sandbox = createSandboxContentService(builder);
    retrieveConfiguration();
  }

  protected void loadConfig(File path) throws IOException {
    config = ContentConfig.load(path);
  }

  // This method assumes a builder that's already been used to create the standard
  // ShoppingContent service object, so we'll just modify the path if needed.
  protected ShoppingContent createSandboxContentService(ShoppingContent.Builder builder) {
    return ContentWorkflowSample.createSandboxContentService(builder);
  }

  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory, ShoppingContentScopes.all(), config);
  }

  protected void retrieveConfiguration() throws IOException {
    ContentWorkflowSample.retrieveConfiguration(content, config);
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
