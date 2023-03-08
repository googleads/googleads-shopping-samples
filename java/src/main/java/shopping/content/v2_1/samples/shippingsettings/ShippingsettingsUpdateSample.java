package shopping.content.v2_1.samples.shippingsettings;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.ShippingSettings;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that updates the shipping settings for the current Merchant Center account. */
public class ShippingsettingsUpdateSample extends ContentSample {
  public ShippingsettingsUpdateSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      ShippingSettings newSettings = ExampleShippingSettingsFactory.create();

      content
          .shippingsettings()
          .update(config.getMerchantId(), config.getMerchantId(), newSettings)
          .execute();
      ShippingSettings response =
          content.shippingsettings().get(config.getMerchantId(), config.getMerchantId()).execute();
      System.out.println("Set the following shipping information:");
      ShippingsettingsUtils.printShippingSettings(response);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ShippingsettingsUpdateSample(args).execute();
  }
}
