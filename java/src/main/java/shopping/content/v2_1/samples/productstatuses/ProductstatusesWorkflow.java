package shopping.content.v2_1.samples.productstatuses;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/** Sample that runs through an entire example workflow using the Accountstatuses service. */
public class ProductstatusesWorkflow extends ContentWorkflowSample {
  private ProductstatusesWorkflow(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new ProductstatusesWorkflow(content, sandbox, config).execute();
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");

    if (config.getIsMCA()) {
      System.out.println(
          "The Merchant Center account is an MCA, so not running the Productstatuses workflow.");
      return;
    }

    System.out.println("Running Productstatuses service workflow:");
    System.out.println();
    System.out.println("Listing all product statuses for the current account.");
    ProductstatusesListSample.listProductStatusesForMerchant(config.getMerchantId(), content);
  }

  public static void main(String[] args) throws IOException {
    CommandLine parsedArgs = BaseOption.parseOptions(args);
    File configPath = null;
    if (!NO_CONFIG.isSet(parsedArgs)) {
      configPath = BaseOption.checkedConfigPath(parsedArgs);
    }
    ContentConfig config = ContentConfig.load(configPath);

    ShoppingContent.Builder builder = createStandardBuilder(parsedArgs, config);
    ShoppingContent content = createService(builder);
    ShoppingContent sandbox = createSandboxContentService(builder);
    retrieveConfiguration(content, config);

    try {
      new ProductstatusesWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
