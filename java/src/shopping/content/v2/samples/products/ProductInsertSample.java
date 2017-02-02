package shopping.content.v2.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Product;

import java.io.IOException;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that inserts a product. The product created here is used in other samples.
 */
public class ProductInsertSample extends ContentSample {
  public ProductInsertSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    // Create a product with ID 'online:en:GB:book123'
    Product product = ExampleProductFactory.create(config, "online", "en", "GB", "book123");

    try {
      Product result = content.products().insert(this.config.getMerchantId(), product).execute();
      printWarnings(result.getWarnings());
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductInsertSample().execute();
  }
}
