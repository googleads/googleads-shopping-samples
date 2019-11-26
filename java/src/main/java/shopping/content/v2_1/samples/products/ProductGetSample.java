package shopping.content.v2_1.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Product;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample that shows how to retrieve the details of the product that we inserted with the
 * ProductInsert sample.
 */
public class ProductGetSample extends ContentSample {
  public ProductGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();
    try {
      // We create a product with ID online:en:GB:book123 in the ProductInsert sample
      Product product =
          content
              .products()
              .get(this.config.getMerchantId(), ExampleProductFactory.sampleProductId())
              .execute();
      System.out.printf("%s %s\n", product.getId(), product.getTitle());
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        System.out.println("The item was not found. Try running the ProductInsert sample first.");
      } else {
        checkGoogleJsonResponseException(e);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductGetSample(args).execute();
  }
}
