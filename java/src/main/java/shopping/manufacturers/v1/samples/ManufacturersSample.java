package shopping.manufacturers.v1.samples;

import com.google.api.services.manufacturers.v1.ManufacturerCenter;
import com.google.api.services.manufacturers.v1.ManufacturerCenterScopes;
import com.google.api.services.manufacturers.v1.model.Attributes;
import com.google.api.services.manufacturers.v1.model.Issue;
import com.google.api.services.manufacturers.v1.model.Product;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.util.List;
import shopping.common.Authenticator;
import shopping.common.BaseSample;

/** Base class for the Manufacturer Center API samples. */
public abstract class ManufacturersSample extends BaseSample {
  protected ManufacturersConfig config;
  protected ManufacturerCenter manufacturers;

  public ManufacturersSample(String[] args) throws IOException {
    super(args);
    ManufacturerCenter.Builder builder =
        new ManufacturerCenter.Builder(httpTransport, jsonFactory, initializer)
            .setApplicationName("Manufacturer Center API Samples");
    manufacturers = createService(builder);
  }

  @Override
  protected void loadConfig(File path) throws IOException {
    config = ManufacturersConfig.load(path);
  }

  @Override
  protected Authenticator loadAuthentication() throws IOException {
    return new Authenticator(httpTransport, jsonFactory, ManufacturerCenterScopes.all(), config);
  }

  /* Unlike the Content API, the Manufacturer Center API doesn't take the ID directly, but
   * it must be prepended with 'accounts/', so we'll call this helper function instead of
   * the method on the configuration object whenever we need that information.
   */
  protected String getManufacturerId() {
    return "accounts/" + config.getManufacturerId();
  }

  protected void printProduct(Product product) {
    System.out.printf("Product \"%s\"%n", product.getName());

    System.out.println("  Attributes:");
    printAttributes(product.getAttributes(), "  ");

    printIssues(product.getIssues(), "  ");
    System.out.println();
  }

  protected void printIssues(List<Issue> issues, String prefix) {
    if (issues == null) {
      return;
    }
    System.out.printf(prefix + "There are %d issue(s):%n", issues.size());
    for (Issue issue : issues) {
      System.out.print(prefix + "- ");
      System.out.printf("(%s, %s) ", issue.getSeverity(), issue.getResolution());
      if (issue.getAttribute() != null) {
        System.out.printf("[%s] ", issue.getAttribute());
      }
      System.out.println(issue.getType() + ": " + issue.getTitle());
      System.out.println(prefix + "  " + issue.getDescription());
    }
  }

  protected void printAttributes(Attributes attributes, String prefix) {
    if (attributes == null) {
      return;
    }
    System.out.printf(prefix + "- Title: %s%n", attributes.getTitle());
    System.out.printf(prefix + "- Brand: %s%n", attributes.getBrand());
    System.out.printf(
        prefix + "- Global Trade Item Number(s): %s%n", Joiner.on(", ").join(attributes.getGtin()));
    if (attributes.getMpn() != null) {
      System.out.printf(prefix + "- Manufacturer Part Number: %s%n", attributes.getMpn());
    }
    if (attributes.getProductName() != null) {
      System.out.printf(prefix + "- Product name: %s%n", attributes.getProductName());
    }
    if (attributes.getProductLine() != null) {
      System.out.printf(prefix + "- Product line: %s%n", attributes.getProductLine());
    }
    if (attributes.getProductPageUrl() != null) {
      System.out.printf(prefix + "- Product page: %s%n", attributes.getProductPageUrl());
    }
  }
}
