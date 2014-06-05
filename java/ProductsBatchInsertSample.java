import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.api.services.content.model.ProductsCustomBatchResponse;
import com.google.api.services.content.model.ProductsCustomBatchResponseEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sample that shows batching product inserts.
 */
public class ProductsBatchInsertSample extends BaseSample {
  private static final int PRODUCT_COUNT = 10;

  @Override
  public void execute() throws IOException {
    List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries =
        new ArrayList<ProductsCustomBatchRequestEntry>();
    ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
    for (int i = 0; i < PRODUCT_COUNT; i++) {
      // Create a product with ID 'online:en:GB:book{i}'
      Product product = ExampleProductFactory.create("online", "en", "GB", "book" + i);
      ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
      entry.setBatchId((long) i);
      entry.setMerchantId(merchantId);
      entry.setProduct(product);
      entry.setMethod("insert");
      productsBatchRequestEntries.add(entry);
    }
    batchRequest.setEntries(productsBatchRequestEntries);
    ProductsCustomBatchResponse batchResponse =
        content.products().custombatch(batchRequest).execute();

    for (ProductsCustomBatchResponseEntry entry : batchResponse.getEntries()) {
      Product product = entry.getProduct();
      System.out.printf("Inserted %s with %d warnings%n", product.getOfferId(),
          product.getWarnings().size());
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductsBatchInsertSample().execute();
  }
}
