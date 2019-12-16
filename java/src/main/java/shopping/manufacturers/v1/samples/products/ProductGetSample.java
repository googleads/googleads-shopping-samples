package shopping.manufacturers.v1.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.manufacturers.v1.model.Product;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import shopping.manufacturers.v1.samples.ManufacturersSample;

/**
 * Sample that shows how to retrieve the details of a particular product, given its name in the
 * format {target_country}:{content_language}:{product_id}.
 */
public class ProductGetSample extends ManufacturersSample {
  public ProductGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    // Replace the values below appropriately for the desired product.
    String targetCountry = "US";
    String contentLanguage = "en";
    String productId = "test-product-519";
    String name = targetCountry + ":" + contentLanguage + ":" + productId;
    try {
      Product product =
          manufacturers.accounts().products().get(getManufacturerId(), name)
            .setInclude(ImmutableList.of("ATTRIBUTES", "ISSUES"))
            .execute();
      printProduct(product);
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        System.out.printf(
            "The product %s for country %s and language %s was not found.%n",
            productId, targetCountry, contentLanguage);
      } else {
        checkGoogleJsonResponseException(e);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductGetSample(args).execute();
  }
}
