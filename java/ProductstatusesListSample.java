import com.google.api.services.content.ShoppingContent.Productstatuses.List;
import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductstatusesListResponse;

import java.io.IOException;

/**
 * Sample that gets the status of each of the products for the merchant. If there is more than one
 * page of results, we fetch each page in turn.
 */
public class ProductstatusesListSample extends BaseSample {
  @Override
  public void execute() throws IOException {
    List productStatusesList = content.productstatuses().list(merchantId);
    ProductstatusesListResponse page = productStatusesList.execute();
    while ((page.getResources() != null) && !page.getResources().isEmpty()) {
      for (ProductStatus productStatus : page.getResources()) {
        System.out.printf("%s %s\n", productStatus.getProductId(), productStatus.getTitle());
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      productStatusesList.setPageToken(page.getNextPageToken());
      page = productStatusesList.execute();
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductstatusesListSample().execute();
  }
}
