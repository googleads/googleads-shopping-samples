package shopping.content.v2.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountsListResponse;
import java.io.IOException;
import java.math.BigInteger;

import shopping.content.v2.samples.ContentSample;

/** Sample that lists the Merchant Center subaccounts for the given MCA. */
public class AccountsListSample extends ContentSample {
  public AccountsListSample(String[] args) throws IOException {
    super(args);
  }

  static void listAccountsForMerchant(BigInteger merchantId, ShoppingContent content)
      throws IOException {
    System.out.printf("Listing sub-accounts for Merchant Center %s:%n", merchantId);
    ShoppingContent.Accounts.List listAccounts = content.accounts().list(merchantId);
    do {
      AccountsListResponse page = listAccounts.execute();
      if (page.getResources() == null) {
        System.out.println("No accounts found.");
        return;
      }
      for (Account account : page.getResources()) {
        AccountUtils.printAccount(account);
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      listAccounts.setPageToken(page.getNextPageToken());
    } while (true);
  }

  @Override
  public void execute() throws IOException {
    checkMCA();
    try {
      listAccountsForMerchant(config.getMerchantId(), content);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountsListSample(args).execute();
  }
}
