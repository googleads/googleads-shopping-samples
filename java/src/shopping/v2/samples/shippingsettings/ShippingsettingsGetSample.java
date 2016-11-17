package shopping.v2.samples.shippingsettings;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.ShippingSettings;
import java.io.IOException;
import shopping.v2.samples.BaseSample;

/** Sample that retrieves the shipping settings for the current Merchant Center account. */
public class ShippingsettingsGetSample extends BaseSample {
  public ShippingsettingsGetSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    try {
      ShippingSettings settings =
          content.shippingsettings().get(config.getMerchantId(), config.getMerchantId()).execute();
      ShippingsettingsUtils.printShippingSettings(settings);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ShippingsettingsGetSample().execute();
  }
}
