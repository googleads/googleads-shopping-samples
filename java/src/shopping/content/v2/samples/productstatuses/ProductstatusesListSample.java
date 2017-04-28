package shopping.content.v2.samples.productstatuses;

import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductStatusDataQualityIssue;
import com.google.api.services.content.model.ProductstatusesListResponse;

import java.io.IOException;

import java.util.List;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that gets the status of each of the products for the merchant. If there is more than one
 * page of results, we fetch each page in turn.
 */
public class ProductstatusesListSample extends ContentSample {
  public ProductstatusesListSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    ShoppingContent.Productstatuses.List productStatusesList =
        content.productstatuses().list(this.config.getMerchantId());
    do {
      ProductstatusesListResponse page = productStatusesList.execute();
      if (page.getResources() == null) {
        System.out.println("No products found.");
        return;
      }
      for (ProductStatus productStatus : page.getResources()) {
        System.out.printf("- %s %s\n", productStatus.getProductId(), productStatus.getTitle());

        List<ProductStatusDataQualityIssue> issues = productStatus.getDataQualityIssues();
        if (issues != null) {
          System.out.printf("  There are %d data quality issue(s)%n", issues.size());
          for (ProductStatusDataQualityIssue issue : issues) {
            if (issue.getDetail() != null) {
              System.out.printf("  - (%s) [%s] %s%n", issue.getSeverity(), issue.getId(),
                  issue.getDetail());
            } else {
              System.out.printf("  - (%s) [%s]%n", issue.getSeverity(), issue.getId());
            }
          }
        }
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      productStatusesList.setPageToken(page.getNextPageToken());
    } while(true);
  }

  public static void main(String[] args) throws IOException {
    new ProductstatusesListSample().execute();
  }
}
