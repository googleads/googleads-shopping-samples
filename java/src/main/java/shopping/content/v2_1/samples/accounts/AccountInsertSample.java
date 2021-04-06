package shopping.content.v2_1.samples.accounts;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Account;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample that inserts a new MC account into an MCA. Configuration must signal that we're working
 * with an MCA for this to run.
 */
public class AccountInsertSample extends ContentSample {
  public AccountInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkMCA();

    try {
      Account account = AccountUtils.getDefaultAccount();

      System.out.println("Inserting new account.");

      Account result = content.accounts().insert(config.getMerchantId(), account).execute();
      AccountUtils.printAccount(result);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountInsertSample(args).execute();
  }
}
