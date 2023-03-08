package shopping.content.v2_1.samples.accountstatuses;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.AccountStatus;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample that retrieves the accountstatuses information for the current Merchant Center account.
 */
public class AccountstatusGetSample extends ContentSample {
  public AccountstatusGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      AccountStatus accountStatus =
          content.accountstatuses().get(config.getMerchantId(), config.getMerchantId()).execute();
      AccountstatusUtils.printAccountStatus(accountStatus);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccountstatusGetSample(args).execute();
  }
}
