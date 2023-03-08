package shopping.content.v2_1.samples.productstatuses;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.ProductStatus;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;
import shopping.content.v2_1.samples.products.ExampleProductFactory;

/**
 * Sample that shows how to retrieve the status of the product that we inserted with the
 * ProductInsert sample.
 */
public class ProductstatusGetSample extends ContentSample {
  public ProductstatusGetSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    try {
      ProductStatus productStatus =
          content
              .productstatuses()
              .get(this.config.getMerchantId(), ExampleProductFactory.sampleProductId())
              .execute();
      ProductstatusUtils.printProductStatus(productStatus);
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        System.out.println(
            "The item was not found. Try running "
                + "shopping.content.v2.samples.products.ProductInsertSample first.");
      } else {
        checkGoogleJsonResponseException(e);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductstatusGetSample(args).execute();
  }
}
