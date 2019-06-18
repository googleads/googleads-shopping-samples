package shopping.content.v2_1.samples.accounttax;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.AccountTax;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that retrieves the accounttax information for the current Merchant Center account. */
public class AccounttaxGetSample extends ContentSample {
  public AccounttaxGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      AccountTax tax =
          content.accounttax().get(config.getMerchantId(), config.getMerchantId()).execute();
      AccounttaxUtils.printAccountTax(tax);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccounttaxGetSample(args).execute();
  }
}
