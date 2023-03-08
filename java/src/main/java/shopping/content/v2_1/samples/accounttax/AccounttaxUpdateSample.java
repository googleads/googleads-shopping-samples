package shopping.content.v2_1.samples.accounttax;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.AccountTax;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that updates the accounttax information for the current Merchant Center account. */
public class AccounttaxUpdateSample extends ContentSample {
  public AccounttaxUpdateSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      AccountTax newSettings = ExampleAccountTaxFactory.create(config);

      AccountTax response =
          content
              .accounttax()
              .update(config.getMerchantId(), config.getMerchantId(), newSettings)
              .execute();
      System.out.println("Set the following tax information:");
      AccounttaxUtils.printAccountTax(response);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccounttaxUpdateSample(args).execute();
  }
}
