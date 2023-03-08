package shopping.content.v2_1.samples.liasettings;

import java.math.BigInteger;

/** Utility class for storing actions performed on Local Inventory Ads Settings. */
public class LiaUtils {
  /**
   * Get account ID from given {@code liaConfig}. Returns liaConfig.merchantId, or
   * liaConfig.accountId if createSubAccount is set to true, assuming that the config has been
   * updated by {@code LiaAccountWorkflow}).
   */
  public static BigInteger getAccountIdFromLiaConfig(LiaConfig liaConfig) {
    if (liaConfig.getCreateSubAccount()) {
      return liaConfig.getAccountId();
    } else {
      return liaConfig.getMerchantId();
    }
  }
}
