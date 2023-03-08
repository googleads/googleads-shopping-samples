package shopping.content.v2_1.samples.liasettings.gmb;

import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Account;
import com.google.api.services.content.model.AccountGoogleMyBusinessLink;
import com.google.api.services.content.model.GmbAccountsGmbAccount;
import com.google.api.services.content.model.LiasettingsGetAccessibleGmbAccountsResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Utility class for storing actions performed on the GMB section of Local Inventory Ads settings.
 */
public class LiaSettingsGmbUtils {
  /** Helper function to check if given GMB account is accessible to given MC account. */
  public static boolean isGmbAccountAccessible(
      String gmbEmail, ShoppingContent shoppingContent, BigInteger accountId) throws IOException {
    LiasettingsGetAccessibleGmbAccountsResponse response =
        shoppingContent.liasettings().getaccessiblegmbaccounts(accountId, accountId).execute();
    List<GmbAccountsGmbAccount> accessibleGmbAccounts = response.getGmbAccounts();
    if (accessibleGmbAccounts == null) {
      return false;
    }

    for (GmbAccountsGmbAccount gmbAccount : accessibleGmbAccounts) {
      if (gmbAccount.getEmail().equals(gmbEmail)) {
        return true;
      }
    }

    return false;
  }
}
