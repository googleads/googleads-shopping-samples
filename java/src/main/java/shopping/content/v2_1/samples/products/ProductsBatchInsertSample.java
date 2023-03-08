package shopping.content.v2_1.samples.products;

import com.google.api.services.content.model.ProductsCustomBatchResponse;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that shows batching product inserts. */
public class ProductsBatchInsertSample extends ContentSample {
  public ProductsBatchInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    ProductsCustomBatchResponse batchResponse =
        content.products().custombatch(ExampleProductFactory.createBatch(config, "book")).execute();
    ProductUtils.printProductBatchResults(batchResponse);
  }

  public static void main(String[] args) throws IOException {
    new ProductsBatchInsertSample(args).execute();
  }
}
