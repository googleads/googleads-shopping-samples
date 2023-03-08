package shopping.content.v2_1.samples.liasettings.gmb;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.Sleeper;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountGoogleMyBusinessLink;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentWorkflowSample;
import shopping.content.v2_1.samples.liasettings.LiaConfig;
import shopping.content.v2_1.samples.liasettings.LiaUtils;

/** Sample that runs through an entire example workflow using the Accounts service. */
public class LiaRequestGmbAccessWorkflow extends ContentWorkflowSample {
  public LiaRequestGmbAccessWorkflow(
      ShoppingContent content, ShoppingContent sandbox, LiaConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, LiaConfig config)
      throws IOException {
    new LiaRequestGmbAccessWorkflow(content, sandbox, config).execute();
  }

  /**
   * Returns false when the the request has been sent to GMB but requires manual approval. In such
   * case, user needs to go to the GMB website to manually approve the link request before
   * proceeding with other workflows (if any).
   */
  public boolean requestGmbAccess() throws IOException {
    LiaConfig liaConfig = (LiaConfig) config;

    BigInteger accountId = LiaUtils.getAccountIdFromLiaConfig(liaConfig);
    if (accountId == null) {
      throw new IllegalArgumentException(
          "Given config does not contain a valid account ID. Please make sure you have set"
              + " merchantId correctly, or have run LiaAccountWorkflow if createSubAccount is set"
              + " to true. Config:"
              + config.toString());
    }

    String gmbEmail = liaConfig.getGmbEmail();
    if (gmbEmail == null) {
      System.out.println(
          "No gmbEmail specified in config. Skipping remaining parts of the workflow.");
      return true;
    }

    Account account = content.accounts().get(accountId, accountId).execute();
    AccountGoogleMyBusinessLink gmbLink = account.getGoogleMyBusinessLink();
    if (gmbLink != null && gmbEmail.equals(gmbLink.getGmbEmail())) {
      System.out.printf(
          "GMB Account: %s has already been linked and one of its listing groups has been"
              + " chosen.%nCurrent status: %s.%n",
          gmbEmail, gmbLink.getStatus());
      return true;
    }

    if (LiaSettingsGmbUtils.isGmbAccountAccessible(gmbEmail, content, accountId)) {
      System.out.printf(
          "The access to GMB account: %s has already been approved. Skipping link request.%n",
          gmbEmail);
    } else {
      content.liasettings().requestgmbaccess(accountId, accountId, gmbEmail).execute();
      System.out.printf("GMB access request sent for %s%n", gmbEmail);

      // The GMB side has some latency on auto-link, so let's start retrying 5s later after the
      // first failure until reached 30s, with a maximum interval of 10s.
      ExponentialBackOff backOff =
          new ExponentialBackOff.Builder()
              .setInitialIntervalMillis(5000)
              .setMaxIntervalMillis(10000)
              .setMaxElapsedTimeMillis(30000)
              .build();
      boolean autoLinkTriggered = false;

      // Keep trying to check if the GMB auto-linking has been in place. Exit after the linkage is
      // found or get to maximum retry time (30s).
      while (true) {
        try {
          if (BackOffUtils.next(Sleeper.DEFAULT, backOff)) {
            System.out.printf("Auto-link not detected. Retrying...%n");
          } else {
            System.out.printf("Auto-link not detected after maximum retry timeout.%n");
            break;
          }
        } catch (InterruptedException ie) {
          // No-op; just go straight into retry if interrupted.
        }
        if (LiaSettingsGmbUtils.isGmbAccountAccessible(gmbEmail, content, accountId)) {
          autoLinkTriggered = true;
          break;
        }
      }

      if (!autoLinkTriggered) {
        System.out.printf(
            "GMB auto linking is not working, skipping remaining parts of the workflow. Please go"
                + " to the GMB website to manually approve the request and choose the desired"
                + " location group in Merchant Center after approval.%n");
        return false;
      } else {
        System.out.printf("GMB account %s is auto-linked now.%n", gmbEmail);
      }
    }

    // Refresh the account as the account could be changed during the execution.
    account = content.accounts().get(accountId, accountId).execute();
    AccountGoogleMyBusinessLink newGmbLink =
        new AccountGoogleMyBusinessLink().setGmbEmail(gmbEmail);
    account.setGoogleMyBusinessLink(newGmbLink);
    content.accounts().update(accountId, accountId, account).execute();
    System.out.printf("GMB link to %s set for Account %s.%n", gmbEmail, accountId);
    return true;
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");
    System.out.println("Running LIA Request GMB Access workflow:");
    System.out.println();

    requestGmbAccess();
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
      new LiaRequestGmbAccessWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
