package shopping.content.v2.samples.inventory;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.InventorySetRequest;
import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.Product;
import java.io.IOException;
import shopping.content.v2.samples.ContentSample;

/** Sample that modifies a product. We modify the product added in ProductInsertSample. */
public class InventorySetSample extends ContentSample {
  public InventorySetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    String offerId = "online:en:GB:book123";

    Price price = new Price();
    price.setValue("4.50");
    price.setCurrency("GBP");

    InventorySetRequest request = new InventorySetRequest();
    request.setPrice(price);
    request.setAvailability("out of stock");

    System.out.printf(
        "Setting information for %s: price %s %s, availability %s%n",
        offerId,
        request.getPrice().getValue(),
        request.getPrice().getCurrency(),
        request.getAvailability());

    try {
      content.inventory().set(config.getMerchantId(), "online", offerId, request).execute();
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        System.out.println(
            "The item was not found. Try running "
                + "shopping.content.v2.samples.products.ProductInsertSample first.");
      } else {
        checkGoogleJsonResponseException(e);
      }
    }

    System.out.println("Inventory.set call succeeded. Fetching product information.");

    try {
      Product product = content.products().get(config.getMerchantId(), offerId).execute();
      System.out.printf(
          "New product information: price %s %s, availability %s%n",
          product.getPrice().getValue(),
          product.getPrice().getCurrency(),
          product.getAvailability());
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new InventorySetSample(args).execute();
  }
}
