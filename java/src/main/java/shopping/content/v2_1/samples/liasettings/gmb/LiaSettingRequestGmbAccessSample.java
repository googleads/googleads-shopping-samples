package shopping.content.v2_1.samples.liasettings.gmb;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import java.math.BigInteger;
import shopping.content.v2_1.samples.liasettings.LiaSample;

/** Sample that requests GMB access for given GMB email and account. */
public class LiaSettingRequestGmbAccessSample extends LiaSample {

  public LiaSettingRequestGmbAccessSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      BigInteger merchantId = this.config.getMerchantId();
      String gmbEmail = getLiaConfig().getGmbEmail();
      content.liasettings().requestgmbaccess(merchantId, merchantId, gmbEmail).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new LiaSettingRequestGmbAccessSample(args).execute();
  }
}
