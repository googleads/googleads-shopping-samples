package shopping.content.v2.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountAdwordsLink;
import com.google.api.services.content.model.AccountUser;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.ParseException;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that patches changes to the users and AdWords links for the given account.
 */
public class AccountPatchSample extends ContentSample {
  public AccountPatchSample(String[] args) throws IOException, ParseException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      // First we need to get the current account information, since we're going to be sending
      // back the changes.
      Account account = content.accounts().get(config.getMerchantId(), config.getMerchantId())
          .execute();
      AccountUtils.printAccount(account);

      Boolean changed = false;

      // We can adjust the account information directly and send it back in:
      if (config.getAccountSampleUser() != null && !config.getAccountSampleUser().equals("")) {
        System.out.printf("Adding new user %s%n", config.getAccountSampleUser());

        AccountUser newUser = new AccountUser();
        newUser.setAdmin(false);
        newUser.setEmailAddress(config.getAccountSampleUser());

        account.getUsers().add(newUser);
        changed = true;
      }
      if (!config.getAccountSampleAdWordsCID().equals(BigInteger.ZERO)) {
        System.out.printf("Linking AdWords CID %s%n", config.getAccountSampleAdWordsCID());

        AccountAdwordsLink newLink = new AccountAdwordsLink();
        newLink.setAdwordsId(config.getAccountSampleAdWordsCID());
        newLink.setStatus("active");

        account.getAdwordsLinks().add(newLink);
        changed = true;
      }

      if (!changed) {
        System.out.println("No sample user or AdWords CID in configuration file.");
        return;
      }

      account = content.accounts().patch(config.getMerchantId(), config.getMerchantId(), account)
        .execute();
      System.out.printf("%nAccount information after adding new user/link:%n");
      AccountUtils.printAccount(account);

        // We can also create a new Account object, setting only the fields we want to change.
      Account patchedAccount = new Account();

      if (config.getAccountSampleUser() != null && !config.getAccountSampleUser().equals("")) {
        System.out.printf("Removing new user %s%n", config.getAccountSampleUser());
        List<AccountUser> users = new ArrayList<AccountUser>();


        for (AccountUser user : account.getUsers()) {
          if (!user.getEmailAddress().equals(config.getAccountSampleUser())) {
            users.add(user);
          }
        }

        patchedAccount.setUsers(users);
      }
      if (!config.getAccountSampleAdWordsCID().equals(BigInteger.ZERO)) {
        System.out.printf("Removing new AdWords link for %s%n",
            config.getAccountSampleAdWordsCID());
        List<AccountAdwordsLink> links = new ArrayList<AccountAdwordsLink>();


        for (AccountAdwordsLink link : account.getAdwordsLinks()) {
          if (!link.getAdwordsId().equals(config.getAccountSampleAdWordsCID())) {
            links.add(link);
          }
        }

        patchedAccount.setAdwordsLinks(links);
      }

      account = content.accounts()
          .patch(config.getMerchantId(), config.getMerchantId(), patchedAccount)
          .execute();
      System.out.printf("%nAccount information after removing new user/link:%n");
      AccountUtils.printAccount(account);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException, ParseException {
    new AccountPatchSample(args).execute();
  }
}
