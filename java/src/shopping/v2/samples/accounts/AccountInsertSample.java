package shopping.v2.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shopping.v2.samples.BaseSample;

/**
 * Sample that inserts a new MC account into an MCA.  Configuration must signal that we're
 * working with an MCA for this to run.
 */
public class AccountInsertSample extends BaseSample {
  public AccountInsertSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkMCA();

    try {
      Account account = new Account();
      account.setName(AccountUtils.SAMPLE_ACCOUNT_NAME);

      System.out.println("Inserting new account.");

      Account result = content.accounts().insert(config.getMerchantId(), account)
          .execute();
      AccountUtils.printAccount(result);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountInsertSample().execute();
  }
}
