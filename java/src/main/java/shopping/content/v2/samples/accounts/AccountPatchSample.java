package shopping.content.v2.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountAdwordsLink;
import com.google.api.services.content.model.AccountUser;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2.samples.ContentConfig;
import shopping.content.v2.samples.ContentSample;

/** Sample that patches changes to the users and AdWords links for the given account. */
public class AccountPatchSample extends ContentSample {
  public AccountPatchSample(String[] args) throws IOException {
    super(args);
  }

  static void patchAccountFromConfig(Account account, ShoppingContent content, ContentConfig config)
      throws IOException {
    BigInteger merchantId = config.getMerchantId();
    String user = config.getAccountSampleUser();
    BigInteger adWordsCID = config.getAccountSampleAdWordsCID();
    Boolean changed = false;

    // We can adjust the account information directly and send it back in:
    if (user != null) {
      System.out.printf("Adding new user %s%n", user);

      AccountUser newUser = new AccountUser();
      newUser.setAdmin(false);
      newUser.setEmailAddress(user);

      account.getUsers().add(newUser);
      changed = true;
    }
    if (adWordsCID != null) {
      System.out.printf("Linking AdWords CID %s%n", adWordsCID);

      AccountAdwordsLink newLink = new AccountAdwordsLink();
      newLink.setAdwordsId(adWordsCID);
      newLink.setStatus("active");

      account.getAdwordsLinks().add(newLink);
      changed = true;
    }

    if (!changed) {
      System.out.println("No sample user or AdWords CID provided.");
      return;
    }

    account = content.accounts().patch(merchantId, account.getId(), account).execute();
    System.out.printf("%nAccount information after adding new user/link:%n");
    AccountUtils.printAccount(account);

    // We can also create a new Account object, setting only the fields we want to change.
    Account patchedAccount = new Account();

    if (user != null) {
      System.out.printf("Removing new user %s%n", user);
      List<AccountUser> users = new ArrayList<AccountUser>();

      for (AccountUser u : account.getUsers()) {
        if (!u.getEmailAddress().equals(user)) {
          users.add(u);
        }
      }

      patchedAccount.setUsers(users);
    }
    if (adWordsCID != null) {
      System.out.printf("Removing new AdWords link for %s%n", adWordsCID);
      List<AccountAdwordsLink> links = new ArrayList<AccountAdwordsLink>();

      for (AccountAdwordsLink link : account.getAdwordsLinks()) {
        if (!link.getAdwordsId().equals(adWordsCID)) {
          links.add(link);
        }
      }

      patchedAccount.setAdwordsLinks(links);
    }

    account = content.accounts().patch(merchantId, account.getId(), patchedAccount).execute();
    System.out.printf("%nAccount information after removing new user/link:%n");
    AccountUtils.printAccount(account);
  }

  @Override
  public void execute() throws IOException {
    try {
      // First we need to get the current account information, since we're going to be sending
      // back the changes.
      Account account =
          content.accounts().get(config.getMerchantId(), config.getMerchantId()).execute();
      AccountUtils.printAccount(account);

      patchAccountFromConfig(account, content, config);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountPatchSample(args).execute();
  }
}
