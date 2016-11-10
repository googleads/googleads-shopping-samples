package shopping.v2.samples.accountstatuses;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.AccountStatus;
import com.google.api.services.content.model.AccountstatusesListResponse;
import java.io.IOException;
import shopping.v2.samples.BaseSample;

/**
 * Sample that gets the account statuses for all subaccounts of the current Merchant Center
 * account.  Requires a multi-client account.
 */
public class AccountstatusesListSample extends BaseSample {
  public AccountstatusesListSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkMCA();

    try {
      ShoppingContent.Accountstatuses.List accountStatusesList =
          content.accountstatuses().list(this.config.getMerchantId());
      do {
        AccountstatusesListResponse page = accountStatusesList.execute();
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
    new AccountstatusesListSample().execute();
  }
}
