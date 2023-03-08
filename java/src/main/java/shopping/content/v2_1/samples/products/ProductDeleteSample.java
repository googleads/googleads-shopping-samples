package shopping.content.v2_1.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample of deleting a product. It will delete the product that is created by the ProductInsert
 * sample.
 */
public class ProductDeleteSample extends ContentSample {
  public ProductDeleteSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();
    // The shopping.content.v2.samples.products.ProductInsertSample creates a product with ID
    // online:en:GB:book123, so that is the ID we will delete here.
    try {
      content.products().delete(this.config.getMerchantId(), "online:en:GB:book123").execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductDeleteSample(args).execute();
  }
}
