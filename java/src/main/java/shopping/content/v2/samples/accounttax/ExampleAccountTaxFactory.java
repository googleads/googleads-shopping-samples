package shopping.content.v2.samples.accounttax;

import com.google.api.services.content.model.AccountTax;
import com.google.api.services.content.model.AccountTaxTaxRule;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import java.util.List;
import shopping.content.v2.samples.ContentConfig;

/** Factory for creating new tax settings. */
public class ExampleAccountTaxFactory {
  public static AccountTax create(ContentConfig config) {
    List<AccountTaxTaxRule> rules =
        ImmutableList.of(
            new AccountTaxTaxRule()
                .setCountry("US")
                .setLocationId(new BigInteger("21167"))
                .setUseGlobalRate(true));

    return new AccountTax().setAccountId(config.getMerchantId()).setRules(rules);
  }
}
