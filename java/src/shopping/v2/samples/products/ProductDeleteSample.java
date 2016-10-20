package shopping.v2.samples.products;

import java.io.IOException;
import shopping.v2.samples.BaseSample;

/**
 * Sample of deleting a product. It will delete the product that is created by the ProductInsert
 * sample.
 */
public class ProductDeleteSample extends BaseSample {
  public ProductDeleteSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    // The shopping.v2.samples.products.ProductInsertSample creates a product with ID
    // online:en:GB:book123, so that is the ID we will delete here.
    content.products()
        .delete(merchantId, "online:en:GB:book123")
        .execute();
  }

  public static void main(String[] args) throws IOException {
    new ProductDeleteSample().execute();
  }
}
