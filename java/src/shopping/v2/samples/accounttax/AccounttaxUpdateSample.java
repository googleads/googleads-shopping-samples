package shopping.v2.samples.accounttax;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.AccountTax;
import com.google.api.services.content.model.AccountTaxTaxRule;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import shopping.v2.samples.BaseSample;

/** Sample that updates the accounttax information for the current Merchant Center account. */
public class AccounttaxUpdateSample extends BaseSample {
  public AccounttaxUpdateSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    try {
      List<AccountTaxTaxRule> rules = ImmutableList.of(
          new AccountTaxTaxRule()
              .setCountry("US")
              .setLocationId(new BigInteger("21167"))
              .setUseGlobalRate(true));

      AccountTax newSettings =
          new AccountTax().setAccountId(config.getMerchantId()).setRules(rules);

      newSettings =
          content
              .accounttax()
              .update(config.getMerchantId(), config.getMerchantId(), newSettings)
              .execute();
      System.out.println("Set the following tax information:");
      AccounttaxUtils.printAccountTax(newSettings);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new AccounttaxUpdateSample().execute();
  }
}
