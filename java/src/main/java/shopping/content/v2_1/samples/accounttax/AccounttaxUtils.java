package shopping.content.v2_1.samples.accounttax;

import com.google.api.services.content.model.AccountTax;
import com.google.api.services.content.model.AccountTaxTaxRule;

/** Utility class for working with AccountTax resources. */
public class AccounttaxUtils {
  public static void printAccountTax(AccountTax tax) {
    System.out.printf("Tax information for account %d:\n", tax.getAccountId());
    if (tax.getRules() == null || tax.getRules().isEmpty()) {
      System.out.println("- No tax information, so no tax is charged.");
    } else {
      for (AccountTaxTaxRule rule : tax.getRules()) {
        if (rule.getRatePercent() != null && !rule.getRatePercent().equals("")) {
          System.out.printf(
              "- For location %d in country %s, rate is %s%%.\n",
              rule.getLocationId(), rule.getCountry(), rule.getRatePercent());
        }
        if (rule.getUseGlobalRate() == Boolean.TRUE) {
          System.out.printf(
              "- For location %d in country %s, using global tax table rate.\n",
              rule.getLocationId(), rule.getCountry());
        }
        if (rule.getShippingTaxed() == Boolean.TRUE) {
          System.out.println(" Note: Shipping charges are also taxed.");
        }
      }
    }
  }
}
