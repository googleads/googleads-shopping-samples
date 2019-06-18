package shopping.content.v2_1.samples.accounttax;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.AccountTax;
import com.google.api.services.content.model.AccounttaxListResponse;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/** Sample that runs through an entire example workflow using the Accounttax service. */
public class AccounttaxWorkflow extends ContentWorkflowSample {
  private AccounttaxWorkflow(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new AccounttaxWorkflow(content, sandbox, config).execute();
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");
    System.out.println("Running Accounttax service workflow:");
    System.out.println();

    System.out.print("Retrieving original tax settings...");
    AccountTax originalSettings =
        content.accounttax().get(config.getMerchantId(), config.getMerchantId()).execute();
    System.out.println("done.");
    System.out.println("Original tax settings:");
    AccounttaxUtils.printAccountTax(originalSettings);

    System.out.print("Setting new tax settings...");
    AccountTax newSettings = ExampleAccountTaxFactory.create(config);
    AccountTax response =
        content
            .accounttax()
            .update(config.getMerchantId(), config.getMerchantId(), newSettings)
            .execute();
    System.out.println("done.");
    System.out.println("Current tax settings:");
    AccounttaxUtils.printAccountTax(response);

    System.out.print("Restoring original tax settings...");
    response =
        content
            .accounttax()
            .update(config.getMerchantId(), config.getMerchantId(), originalSettings)
            .execute();
    System.out.println("done.");
    System.out.println("Current tax settings:");
    AccounttaxUtils.printAccountTax(response);

    if (!config.getIsMCA()) {
      return;
    }

    System.out.println("Listing tax settings for all sub-accounts:");
    ShoppingContent.Accounttax.List accountTaxList =
        content.accounttax().list(config.getMerchantId());
    do {
      AccounttaxListResponse page = accountTaxList.execute();
      if (page.getResources() == null) {
        System.out.println("No accounts found.");
        return;
      }
      for (AccountTax settings : page.getResources()) {
        AccounttaxUtils.printAccountTax(settings);
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      accountTaxList.setPageToken(page.getNextPageToken());
    } while (true);
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
      new AccounttaxWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
