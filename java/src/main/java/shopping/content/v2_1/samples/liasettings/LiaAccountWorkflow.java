package shopping.content.v2_1.samples.liasettings;

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
import shopping.content.v2_1.samples.ContentWorkflowSample;
import shopping.content.v2_1.samples.accounts.AccountUtils;
import shopping.content.v2_1.samples.accounts.AccountsListSample;

/** Sample that runs through an entire example workflow using the Accounts service. */
public class LiaAccountWorkflow extends ContentWorkflowSample {
  public LiaAccountWorkflow(ShoppingContent content, ShoppingContent sandbox, LiaConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, LiaConfig config)
      throws IOException {
    new LiaAccountWorkflow(content, sandbox, config).execute();
  }

  public Account getOrCreateSubAccount() throws IOException {
    LiaConfig liaConfig = (LiaConfig) config;

    System.out.println("Retrieving account information.");
    Account account =
        content.accounts().get(liaConfig.getMerchantId(), liaConfig.getMerchantId()).execute();
    AccountUtils.printAccount(account);

    if (!liaConfig.getIsMCA()) {
      if (liaConfig.getCreateSubAccount()) {
        throw new IllegalArgumentException(
            "createSubAccount is true but given merchantId is not a MCA");
      }
      return account;
    }

    AccountsListSample.listAccountsForMerchant(liaConfig.getMerchantId(), content);

    String subAccountConfigPath = liaConfig.getSubAccountConfigPath();
    Account subAccount;
    if (subAccountConfigPath == null || subAccountConfigPath.isEmpty()) {
      subAccount = AccountUtils.getDefaultAccount();
    } else {
      subAccount = AccountUtils.loadAccountFromJson(subAccountConfigPath);
    }

    System.out.println("Creating new sub-account.");
    subAccount = content.accounts().insert(liaConfig.getMerchantId(), subAccount).execute();
    AccountUtils.printAccount(subAccount);

    BigInteger accountId = subAccount.getId();
    System.out.printf("Retrieving new sub-account %s.%n", accountId);
    // Newly created accounts may not be immediately accessible, so retry until available
    // or until our back off strategy runs out.
    ExponentialBackOff backOff =
        new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(5000)
            .setMaxIntervalMillis(30000)
            .build();
    subAccount =
        retryFailures(content.accounts().get(liaConfig.getMerchantId(), accountId), backOff);
    AccountUtils.printAccount(subAccount);

    return subAccount;
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");
    System.out.println("Running LIA Account workflow:");
    System.out.println();

    getOrCreateSubAccount();
  }

  public static void main(String[] args) throws IOException {
    CommandLine parsedArgs = BaseOption.parseOptions(args);
    File configPath = null;
    if (!NO_CONFIG.isSet(parsedArgs)) {
      configPath = BaseOption.checkedConfigPath(parsedArgs);
    }
    configPath = BaseOption.checkedConfigPath(parsedArgs);
    LiaConfig config = LiaConfig.load(configPath);

    ShoppingContent.Builder builder = createStandardBuilder(parsedArgs, config);
    ShoppingContent content = createService(builder);
    ShoppingContent sandbox = createSandboxContentService(builder);
    retrieveConfiguration(content, config);

    try {
      new LiaAccountWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
