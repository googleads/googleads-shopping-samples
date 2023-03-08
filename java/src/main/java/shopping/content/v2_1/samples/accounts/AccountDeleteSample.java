package shopping.content.v2_1.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountsListResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that deletes new MC accounts created by AccountInsertSample. */
public class AccountDeleteSample extends ContentSample {
  public AccountDeleteSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkMCA();

    try {
      List<BigInteger> accountIds = getAccountIdsWithName(AccountUtils.SAMPLE_ACCOUNT_NAME);
      if (accountIds.isEmpty()) {
        System.out.printf("No accounts found with name %s.%n", AccountUtils.SAMPLE_ACCOUNT_NAME);
        return;
      }
      for (BigInteger accountId : accountIds) {
        System.out.printf("Deleting account with ID %d%n", accountId);
        content.accounts().delete(config.getMerchantId(), accountId).execute();
      }
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  private List<BigInteger> getAccountIdsWithName(String name) throws IOException {
    List<BigInteger> results = new ArrayList<>();
    ShoppingContent.Accounts.List listAccounts = content.accounts().list(config.getMerchantId());
    do {
      AccountsListResponse page = listAccounts.execute();
      for (Account account : page.getResources()) {
        if (account.getName().equals(name)) {
          results.add(account.getId());
        }
      }
      if (page.getNextPageToken() == null) {
        return results;
      }
      listAccounts.setPageToken(page.getNextPageToken());
    } while (true);
  }

  public static void main(String[] args) throws IOException {
    new AccountDeleteSample(args).execute();
  }
}
