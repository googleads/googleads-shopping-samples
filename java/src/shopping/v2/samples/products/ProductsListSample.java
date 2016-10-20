package shopping.v2.samples.products;

import com.google.api.services.content.ShoppingContent.Products.List;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsListResponse;

import java.io.IOException;
import shopping.v2.samples.BaseSample;

/**
 * Sample that gets a list of all of the products for the merchant. If there is more than one page
 * of results, we fetch each page in turn.
 */
public class ProductsListSample extends BaseSample {
  public ProductsListSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    List productsList = content.products().list(merchantId);
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
    new ProductsListSample().execute();
  }
}
