package shopping.content.v2_1.samples.products;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.api.services.content.model.ProductsCustomBatchResponse;
import com.google.api.services.content.model.ProductsCustomBatchResponseEntry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/**
 * Sample that runs through an entire example workflow using the Products service.
 *
 * <p>It also includes a use of the Inventory service.
 */
public class ProductsWorkflow extends ContentWorkflowSample {

  private ProductsWorkflow(ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new ProductsWorkflow(content, sandbox, config).execute();
  }

  private ProductsCustomBatchRequest deleteBatch(ProductsCustomBatchResponse batchResponse) {
    List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries = new ArrayList<>();
    for (ProductsCustomBatchResponseEntry e : batchResponse.getEntries()) {
      if (e.getProduct() != null) {
        productsBatchRequestEntries.add(
            new ProductsCustomBatchRequestEntry()
                .setBatchId(e.getBatchId())
                .setMerchantId(config.getMerchantId())
                .setProductId(e.getProduct().getId())
                .setMethod("delete"));
      }
    }
    return new ProductsCustomBatchRequest().setEntries(productsBatchRequestEntries);
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");

    if (config.getIsMCA()) {
      System.out.println(
          "The Merchant Center account is an MCA, so not running the Products workflow.");
      return;
    }

    Random rand = new Random();
    String baseName = "book" + rand.nextInt(5000);

    System.out.println("Running Products service workflow:");
    System.out.println();
    System.out.println("Listing current products:");
    ProductsListSample.listProductsForMerchant(config.getMerchantId(), content);

    System.out.print("Inserting new product... ");
    Product product = ExampleProductFactory.create(config, baseName);
    Product newBook = content.products().insert(config.getMerchantId(), product).execute();
    System.out.println("done.");

    System.out.println("Retrieving new product:");
    // Newly created products may not be immediately accessible, so retry until available
    // or until our back off strategy runs out.
    ExponentialBackOff backOff =
        new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(5000)
            .setMaxIntervalMillis(30000)
            .build();
    Product response =
        retryFailures(content.products().get(config.getMerchantId(), newBook.getId()), backOff);
    ProductUtils.printProduct(response);

    System.out.println("Printing out current product details:");
    response = content.products().get(config.getMerchantId(), newBook.getId()).execute();
    ProductUtils.printProduct(response);

    System.out.println("Inserting a batch of new products:");
    ProductsCustomBatchRequest insertRequest =
        ExampleProductFactory.createBatch(config, baseName + "_");
    ProductUtils.printProductBatchRequest(insertRequest);
    ProductsCustomBatchResponse batchResponse =
        content.products().custombatch(insertRequest).execute();
    ProductUtils.printProductBatchResults(batchResponse);

    System.out.println("Listing current products:");
    ProductsListSample.listProductsForMerchant(config.getMerchantId(), content);

    System.out.println("Deleting new products:");
    content.products().delete(config.getMerchantId(), newBook.getId()).execute();
    System.out.printf("Product %s deleted.%n", newBook.getId());
    ProductsCustomBatchRequest deleteRequest = deleteBatch(batchResponse);
    ProductUtils.printProductBatchRequest(deleteRequest);
    ProductsCustomBatchResponse batchDeleteResponse =
        content.products().custombatch(deleteRequest).execute();
    ProductUtils.printProductBatchResults(batchDeleteResponse);

    System.out.println("Listing current products:");
    ProductsListSample.listProductsForMerchant(config.getMerchantId(), content);
  }

  public static void main(String[] args) throws IOException {
    CommandLine parsedArgs = BaseOption.parseOptions(args);
    File configPath = null;
    if (!NO_CONFIG.isSet(parsedArgs)) {
      configPath = BaseOption.checkedConfigPath(parsedArgs);
    }
    ContentConfig config = ContentConfig.load(configPath);

    ShoppingContent.Builder builder = createStandardBuilder(parsedArgs, config);
    ShoppingContent content = createService(builder);
    ShoppingContent sandbox = createSandboxContentService(builder);
    retrieveConfiguration(content, config);

    try {
      new ProductsWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
