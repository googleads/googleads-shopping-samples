import com.google.api.services.content.ShoppingContent.Products.List;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsListResponse;

import java.io.IOException;

/**
 * Sample demonstrating retrieving only a subset of fields for an item.
 */
public class PartialResponseSample extends BaseSample {
  @Override
  public void execute() throws IOException {
    List productsList = content.products().list(merchantId);

    // Must still select the nextPageToken if you wish to page through results
    productsList.setFields("kind,nextPageToken,resources(id,title)");

    ProductsListResponse page = productsList.execute();
    while ((page.getResources() != null) && !page.getResources().isEmpty()) {
      for (Product product : page.getResources()) {
        System.out.printf("%s %s%n", product.getId(), product.getTitle());
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      productsList.setPageToken(page.getNextPageToken());
      page = productsList.execute();
    }
  }

  public static void main(String[] args) throws IOException {
    new PartialResponseSample().execute();
  }
}
