package shopping.content.v2.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Product;
import java.io.IOException;
import shopping.content.v2.samples.ContentSample;
import shopping.content.v2.samples.ContentUtils;

/** Sample that inserts a product. The product created here is used in other samples. */
public class ProductInsertSample extends ContentSample {
  public ProductInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    // Create a product with ID 'online:en:GB:book123'
    Product product = ExampleProductFactory.create(config, "book123");

    try {
      Product result = content.products().insert(this.config.getMerchantId(), product).execute();
      ContentUtils.printWarnings(result.getWarnings());
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductInsertSample(args).execute();
  }
}
