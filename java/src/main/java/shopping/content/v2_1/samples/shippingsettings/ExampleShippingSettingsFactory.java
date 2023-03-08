package shopping.content.v2_1.samples.shippingsettings;

import com.google.api.services.content.model.DeliveryTime;
import com.google.api.services.content.model.PostalCodeGroup;
import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.RateGroup;
import com.google.api.services.content.model.Service;
import com.google.api.services.content.model.ShippingSettings;
import com.google.api.services.content.model.Value;
import com.google.common.collect.ImmutableList;

/** Factory for creating new shipping settings. */
public class ExampleShippingSettingsFactory {
  public static ShippingSettings create() {
    return new ShippingSettings()
        .setPostalCodeGroups(ImmutableList.<PostalCodeGroup>of())
        .setServices(
            ImmutableList.of(
                new Service()
                    .setName("USPS")
                    .setCurrency("USD")
                    .setDeliveryCountry("US")
                    .setDeliveryTime(
                        new DeliveryTime().setMinTransitTimeInDays(3L).setMaxTransitTimeInDays(7L))
                    .setActive(true)
                    .setRateGroups(
                        ImmutableList.of(
                            new RateGroup()
                                .setApplicableShippingLabels(ImmutableList.<String>of())
                                .setSingleValue(
                                    new Value()
                                        .setFlatRate(
                                            new Price().setValue("5.00").setCurrency("USD")))))));
  }
}
