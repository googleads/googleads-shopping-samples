package shopping.content.v2.samples.productstatuses;

import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductStatusDataQualityIssue;
import java.util.List;

/** Utility class for methods like printing ProductStatus objects. */
public class ProductstatusUtils {
  public static void printProductStatus(ProductStatus productStatus) {
    System.out.printf("- %s %s\n", productStatus.getProductId(), productStatus.getTitle());

    List<ProductStatusDataQualityIssue> issues = productStatus.getDataQualityIssues();
    if (issues != null) {
      System.out.printf("  There are %d data quality issue(s)%n", issues.size());
      for (ProductStatusDataQualityIssue issue : issues) {
        if (issue.getDetail() != null) {
          System.out.printf(
              "  - (%s) [%s] %s%n", issue.getSeverity(), issue.getId(), issue.getDetail());
        } else {
          System.out.printf("  - (%s) [%s]%n", issue.getSeverity(), issue.getId());
        }
      }
    }
  }
}
