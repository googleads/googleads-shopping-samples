package shopping.v2.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountsListResponse;
import java.io.IOException;
import shopping.v2.samples.BaseSample;

/**
 * Sample that lists the Merchant Center subaccounts for the given MCA.
 */
public class AccountsListSample extends BaseSample {
  public AccountsListSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkMCA();
    try {
      ShoppingContent.Accounts.List listAccounts = content.accounts().list(config.getMerchantId());
      do {
        AccountsListResponse page = listAccounts.execute();
        for (Account account : page.getResources()) {
          AccountUtils.printAccount(account);
        }
        if (page.getNextPageToken() == null) {
          break;
        }
        listAccounts.setPageToken(page.getNextPageToken());
      } while(true);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountsListSample().execute();
  }
}
