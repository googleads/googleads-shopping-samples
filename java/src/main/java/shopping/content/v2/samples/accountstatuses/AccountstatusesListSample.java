package shopping.content.v2.samples.accountstatuses;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.AccountStatus;
import com.google.api.services.content.model.AccountstatusesListResponse;
import java.io.IOException;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that gets the account statuses for all subaccounts of the current Merchant Center account.
 * Requires a multi-client account.
 */
public class AccountstatusesListSample extends ContentSample {
  public AccountstatusesListSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkMCA();

    try {
      ShoppingContent.Accountstatuses.List accountStatusesList =
          content.accountstatuses().list(this.config.getMerchantId());
      do {
        AccountstatusesListResponse page = accountStatusesList.execute();
        if (page.getResources() == null) {
          System.out.println("No accounts found.");
          return;
        }
        for (AccountStatus accountStatus : page.getResources()) {
          AccountstatusUtils.printAccountStatus(accountStatus);
        }
        if (page.getNextPageToken() == null) {
          break;
        }
        accountStatusesList.setPageToken(page.getNextPageToken());
      } while (true);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountstatusesListSample(args).execute();
  }
}
