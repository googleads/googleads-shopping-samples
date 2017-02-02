package shopping.content.v2.samples.products;

import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.api.services.content.model.ProductsCustomBatchResponse;
import com.google.api.services.content.model.ProductsCustomBatchResponseEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that shows batching product inserts.
 */
public class ProductsBatchInsertSample extends ContentSample {
  private static final int PRODUCT_COUNT = 10;

  public ProductsBatchInsertSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries =
        new ArrayList<ProductsCustomBatchRequestEntry>();
    ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
    for (int i = 0; i < PRODUCT_COUNT; i++) {
      // Create a product with ID 'online:en:GB:book{i}'
      Product product = ExampleProductFactory.create(config, "online", "en", "GB", "book" + i);
      ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
      entry.setBatchId((long) i);
      entry.setMerchantId(this.config.getMerchantId());
      entry.setProduct(product);
      entry.setMethod("insert");
      productsBatchRequestEntries.add(entry);
    }
    batchRequest.setEntries(productsBatchRequestEntries);
    ProductsCustomBatchResponse batchResponse =
        content.products().custombatch(batchRequest).execute();

    for (ProductsCustomBatchResponseEntry entry : batchResponse.getEntries()) {
      if (entry.getErrors() != null) {
        System.out.printf("Batch entry %d failed%n", entry.getBatchId());
        printErrors(entry.getErrors().getErrors());
      } else {
        Product product = entry.getProduct();
        System.out.printf("Batch entry %d succesfully inserted %s%n", entry.getBatchId(),
            product.getOfferId());
        printWarnings(product.getWarnings());
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductsBatchInsertSample().execute();
  }
}
