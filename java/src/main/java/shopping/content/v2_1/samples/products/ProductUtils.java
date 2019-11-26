package shopping.content.v2_1.samples.products;

import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.api.services.content.model.ProductsCustomBatchResponse;
import com.google.api.services.content.model.ProductsCustomBatchResponseEntry;
import shopping.content.v2_1.samples.ContentUtils;

/** Utility class for methods like printing Product objects. */
public class ProductUtils {
  public static void printProduct(Product product) {
    if (product.getTitle() != null) {
      System.out.printf("- [%s] %s%n", product.getId(), product.getTitle());
    } else {
      System.out.printf("- [%s]%n", product.getId());
    }
    if (product.getPrice() != null) {
      System.out.printf(
          "  Price: %s %s%n", product.getPrice().getValue(), product.getPrice().getCurrency());
    }
    if (product.getAvailability() != null) {
      System.out.printf("  Availability: %s%n", product.getAvailability());
    }
  }

  public static void printProductBatchRequest(ProductsCustomBatchRequest request) {
    if (request.getEntries() == null || request.getEntries().isEmpty()) {
      System.out.println("No entries in custombatch request.");
    }
    for (ProductsCustomBatchRequestEntry entry : request.getEntries()) {
      if (entry.getBatchId() == null) {
        throw new IllegalArgumentException("No batch ID in entry");
      }
      if (entry.getMerchantId() == null) {
        throw new IllegalArgumentException(
            String.format("No merchant ID in batch entry %s", entry.getBatchId()));
      }
      if (entry.getMethod().equals("insert")) {
        if (entry.getProduct() == null) {
          throw new IllegalArgumentException(
              String.format(
                  "No Product configuration in insert batch entry %s", entry.getBatchId()));
        }
        System.out.printf("Batch entry %s, insert succeeded:%n", entry.getBatchId());
        printProduct(entry.getProduct());
      } else if (entry.getMethod().equals("update")) {
        if (entry.getProduct() == null) {
          throw new IllegalArgumentException(
              String.format(
                  "No Product configuration in update batch entry %s", entry.getBatchId()));
        }
        System.out.printf("Batch entry %s, update succeeded:%n", entry.getBatchId());
        printProduct(entry.getProduct());
      } else if (entry.getMethod().equals("get")) {
        if (entry.getProductId() == null) {
          throw new IllegalArgumentException(
              String.format("No Product ID in get batch entry %s", entry.getBatchId()));
        }
        System.out.printf(
            "Batch entry %s: get Product %s%n", entry.getBatchId(), entry.getProductId());
      } else if (entry.getMethod().equals("delete")) {
        if (entry.getProductId() == null) {
          throw new IllegalArgumentException(
              String.format("No Product ID in delete batch entry %s", entry.getBatchId()));
        }
        System.out.printf(
            "Batch entry %s: delete Product %s%n", entry.getBatchId(), entry.getProductId());
      } else {
        throw new IllegalArgumentException(
            String.format(
                "Unknown method in batch entry %s: %s", entry.getBatchId(), entry.getMethod()));
      }
    }
  }

  public static void printProductBatchResults(ProductsCustomBatchResponse response) {
    if (response.getEntries() == null) {
      System.out.println("There were no results from the batch request.");
      return;
    }
    for (ProductsCustomBatchResponseEntry entry : response.getEntries()) {
      if (entry.getErrors() != null) {
        System.out.printf("Errors in batch entry %d:%n", entry.getBatchId());
        ContentUtils.printErrors(entry.getErrors().getErrors());
      } else {
        System.out.printf("Successfully executed batch entry %d%n", entry.getBatchId());
        if (entry.getProduct() != null) {
          printProduct(entry.getProduct());
        }
      }
    }
  }
}
