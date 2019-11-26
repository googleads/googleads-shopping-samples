package shopping.content.v2_1.samples.productstatuses;

import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductStatusDestinationStatus;
import com.google.api.services.content.model.ProductStatusItemLevelIssue;
import java.util.List;

/** Utility class for methods like printing ProductStatus objects. */
public class ProductstatusUtils {
  public static void printProductStatus(ProductStatus productStatus) {
    System.out.printf("- \"%s\" (%s)%n", productStatus.getTitle(), productStatus.getProductId());

    if (productStatus.getDestinationStatuses() != null) {
      System.out.println("  Destination information:");
      for (ProductStatusDestinationStatus status : productStatus.getDestinationStatuses()) {
        System.out.printf(
            "  - Destination %s is %s%n", status.getDestination(), status.getStatus());
      }
    }
    List<ProductStatusItemLevelIssue> issues = productStatus.getItemLevelIssues();
    if (issues != null) {
      System.out.printf("  There are %d issue(s):%n", issues.size());

      for (ProductStatusItemLevelIssue issue : issues) {
        System.out.printf("  - Code: %s%n", issue.getCode());
        System.out.printf("    Description: %s%n", issue.getDescription());
        System.out.printf("    Detailed description: %s%n", issue.getDetail());
        System.out.printf("    Documentation URL: %s%n", issue.getDocumentation());
        System.out.printf("    Resolution: %s%n", issue.getResolution());
        System.out.printf("    Servability effect: %s%n", issue.getServability());
      }
    }
  }
}
