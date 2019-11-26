package shopping.content.v2_1.samples;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.accounts.AccountWorkflow;
import shopping.content.v2_1.samples.accountstatuses.AccountstatusesWorkflow;
import shopping.content.v2_1.samples.accounttax.AccounttaxWorkflow;
import shopping.content.v2_1.samples.datafeeds.DatafeedsWorkflow;
import shopping.content.v2_1.samples.products.ProductsWorkflow;
import shopping.content.v2_1.samples.productstatuses.ProductstatusesWorkflow;
import shopping.content.v2_1.samples.shippingsettings.ShippingsettingsWorkflow;

/** Runs through all the non-Orders services in a single sample. */
public class NonOrdersWorkflow extends ContentWorkflowSample {
  private NonOrdersWorkflow(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public void execute() throws IOException {
    AccountWorkflow.run(content, sandbox, config);
    AccountstatusesWorkflow.run(content, sandbox, config);
    AccounttaxWorkflow.run(content, sandbox, config);
    DatafeedsWorkflow.run(content, sandbox, config);
    ProductsWorkflow.run(content, sandbox, config);
    ProductstatusesWorkflow.run(content, sandbox, config);
    ShippingsettingsWorkflow.run(content, sandbox, config);

    System.out.println("---------------------------------");
    System.out.println("All workflows complete.");
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
      new NonOrdersWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
