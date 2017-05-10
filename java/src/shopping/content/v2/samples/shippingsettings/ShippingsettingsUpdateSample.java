package shopping.content.v2.samples.shippingsettings;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.DeliveryTime;
import com.google.api.services.content.model.PostalCodeGroup;
import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.RateGroup;
import com.google.api.services.content.model.Service;
import com.google.api.services.content.model.ShippingSettings;
import com.google.api.services.content.model.Value;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import shopping.content.v2.samples.ContentSample;

/** Sample that updates the shipping settings for the current Merchant Center account. */
public class ShippingsettingsUpdateSample extends ContentSample {
  public ShippingsettingsUpdateSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      List<RateGroup> newGroups = ImmutableList.of(
        new RateGroup()
            .setApplicableShippingLabels(ImmutableList.<String>of())
            .setSingleValue(
                new Value().setFlatRate(new Price().setValue("5.00").setCurrency("USD"))));

      List<Service> newServices = ImmutableList.of(
          new Service()
              .setName("USPS")
              .setCurrency("USD")
              .setDeliveryCountry("US")
              .setDeliveryTime(
                  new DeliveryTime().setMinTransitTimeInDays(3L).setMaxTransitTimeInDays(7L))
              .setActive(true)
              .setRateGroups(newGroups));

      ShippingSettings newSettings =
          new ShippingSettings()
              .setPostalCodeGroups(ImmutableList.<PostalCodeGroup>of())
              .setServices(newServices);

      content
          .shippingsettings()
          .update(config.getMerchantId(), config.getMerchantId(), newSettings)
          .execute();
      newSettings =
          content.shippingsettings().get(config.getMerchantId(), config.getMerchantId()).execute();
      System.out.println("Set the following shipping information:");
      ShippingsettingsUtils.printShippingSettings(newSettings);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ShippingsettingsUpdateSample(args).execute();
  }
}
