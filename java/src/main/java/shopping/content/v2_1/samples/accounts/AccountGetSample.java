package shopping.content.v2_1.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Account;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that gets the Merchant Center account information for the main MC ID. */
public class AccountGetSample extends ContentSample {
  public AccountGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      Account account =
          content.accounts().get(config.getMerchantId(), config.getMerchantId()).execute();
      AccountUtils.printAccount(account);
      if (!config.getWebsiteUrl().equals(account.getWebsiteUrl())) {
        System.out.println("- Website URL differs from sample configuration:");
        System.out.printf("  - From account: %s%n", account.getWebsiteUrl());
        System.out.printf("  - From config:  %s%n", config.getWebsiteUrl());
      } else {
        System.out.println("- Website URL matches sample configuration.");
      }
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountGetSample(args).execute();
  }
}
