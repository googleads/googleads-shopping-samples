package shopping.content.v2_1.samples.shippingsettings;

import com.google.api.services.content.model.PostalCodeGroup;
import com.google.api.services.content.model.Service;
import com.google.api.services.content.model.ShippingSettings;

/** Utility class for working with Shippingsettings resources. */
public class ShippingsettingsUtils {
  // Currently, this only prints a partial view of the resource, but it's enough to verify
  // the results of get()ing/update()ing.
  public static void printShippingSettings(ShippingSettings settings) {
    System.out.printf("Shipping information for account %d:\n", settings.getAccountId());
    if (settings.getPostalCodeGroups() == null || settings.getPostalCodeGroups().isEmpty()) {
      System.out.println("- No postal code groups.");
    } else {
      System.out.printf("- %d postal code group(s):\n", settings.getPostalCodeGroups().size());
      for (PostalCodeGroup group : settings.getPostalCodeGroups()) {
        System.out.printf("  Postal code group \"%s\":\n", group.getName());
        System.out.printf("  - Country: %s\n", group.getCountry());
        System.out.printf(
            "  - Contains %d postal code ranges.\n", group.getPostalCodeRanges().size());
      }
    }
    if (settings.getServices() == null || settings.getServices().isEmpty()) {
      System.out.println("- No shipping services.");
    } else {
      System.out.printf("- %d shipping service(s):\n", settings.getServices().size());
      for (Service service : settings.getServices()) {
        System.out.printf("  Service \"%s\":\n", service.getName());
        System.out.printf("  - Active: %b\n", service.getActive());
        System.out.printf("  - Country: %s\n", service.getDeliveryCountry());
        System.out.printf("  - Currency: %s\n", service.getCurrency());
        System.out.printf(
            "  - Delivery time: %d - %d days\n",
            service.getDeliveryTime().getMinTransitTimeInDays(),
            service.getDeliveryTime().getMaxTransitTimeInDays());
        System.out.printf(
            "  - %d rate group(s) in this service.\n", service.getRateGroups().size());
      }
    }
  }
}
