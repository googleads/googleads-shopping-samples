package shopping.content.v2.samples;

import static shopping.common.BaseOption.ROOT_URL;

import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContentScopes;
import com.google.api.services.content.model.Error;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.ParseException;
import shopping.common.Authenticator;
import shopping.common.BaseSample;

/**
 * Base class for the Content API samples.
 */
public abstract class ContentSample extends BaseSample {
  protected ContentConfig config;
  protected ShoppingContent content;
  protected ShoppingContent sandbox;

  public ContentSample(String args[]) throws IOException, ParseException {
    super(args);
    content = createContentService();
    sandbox = createSandboxContentService();
  }

  protected void loadConfig(File path) throws IOException {
    config = ContentConfig.load(path);
  }

  protected ShoppingContent createContentService() {
    ShoppingContent.Builder builder =
        new ShoppingContent.Builder(httpTransport, jsonFactory, credential);
    String rootUrl = ROOT_URL.getOptionValue(parsedArgs);
    if (rootUrl != null) {
      builder.setRootUrl(rootUrl);
    }
    return builder.setApplicationName(config.getApplicationName())
        .build();
  }

  protected ShoppingContent createSandboxContentService() {
    ShoppingContent.Builder builder =
        new ShoppingContent.Builder(httpTransport, jsonFactory, credential);
    String rootUrl = ROOT_URL.getOptionValue(parsedArgs);
    if (rootUrl != null) {
      builder.setRootUrl(rootUrl);
    }
    return builder.setApplicationName(config.getApplicationName())
        .setServicePath("content/v2sandbox/")
        .build();
  }

  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory, ShoppingContentScopes.all(), config);
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
