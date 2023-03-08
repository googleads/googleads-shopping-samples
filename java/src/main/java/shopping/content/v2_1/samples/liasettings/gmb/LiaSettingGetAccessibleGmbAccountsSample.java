package shopping.content.v2_1.samples.liasettings.gmb;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.GmbAccountsGmbAccount;
import com.google.api.services.content.model.LiasettingsGetAccessibleGmbAccountsResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import shopping.content.v2_1.samples.liasettings.LiaSample;

/** Sample that requests GMB access for given GMB email and account. */
public class LiaSettingGetAccessibleGmbAccountsSample extends LiaSample {

  public LiaSettingGetAccessibleGmbAccountsSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      BigInteger merchantId = config.getMerchantId();
      LiasettingsGetAccessibleGmbAccountsResponse response =
          content.liasettings().getaccessiblegmbaccounts(merchantId, merchantId).execute();

      List<GmbAccountsGmbAccount> accessibleGmbAccounts = response.getGmbAccounts();
      if (accessibleGmbAccounts != null && !accessibleGmbAccounts.isEmpty()) {
        System.out.printf(
            "Merchant %d has %d accessible GMB accounts:%n",
            merchantId, accessibleGmbAccounts.size());
        for (GmbAccountsGmbAccount gmbAccount : accessibleGmbAccounts) {
          System.out.printf(
              "Name: %s email: %s type: %s listing_count: %d%n",
              gmbAccount.getName(),
              gmbAccount.getEmail(),
              gmbAccount.getType(),
              gmbAccount.getListingCount());
        }
      } else {
        System.out.printf("Merchant %d has no accessible GMB accounts.%n", merchantId);
      }
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new LiaSettingGetAccessibleGmbAccountsSample(args).execute();
  }
}
