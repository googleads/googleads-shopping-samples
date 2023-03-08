package shopping.content.v2_1.samples.accounts;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/** Sample that runs through an entire example workflow using the Accounts service. */
public class AccountWorkflow extends ContentWorkflowSample {
  private AccountWorkflow(ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new AccountWorkflow(content, sandbox, config).execute();
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");
    System.out.println("Running Account service workflow:");
    System.out.println();

    System.out.println("Retrieving account information.");
    Account account =
        content.accounts().get(config.getMerchantId(), config.getMerchantId()).execute();
    AccountUtils.printAccount(account);

    if (!config.getIsMCA()) {
      return;
    }

    AccountsListSample.listAccountsForMerchant(config.getMerchantId(), content);

    Account subaccount = AccountUtils.getDefaultAccount();

    System.out.println("Creating new sub-account.");
    Account result = content.accounts().insert(config.getMerchantId(), subaccount).execute();
    AccountUtils.printAccount(result);

    BigInteger accountId = result.getId();
    System.out.printf("Retrieving new sub-account %s.%n", accountId);
    // Newly created accounts may not be immediately accessible, so retry until available
    // or until our back off strategy runs out.
    ExponentialBackOff backOff =
        new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(5000)
            .setMaxIntervalMillis(30000)
            .build();
    subaccount = retryFailures(content.accounts().get(config.getMerchantId(), accountId), backOff);
    AccountUtils.printAccount(subaccount);

    System.out.printf("Deleting sub-account %s.%n", accountId);
    content.accounts().delete(config.getMerchantId(), accountId).execute();
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
      new AccountWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
