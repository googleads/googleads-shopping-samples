package shopping.manufacturers.v1.samples.products;

import com.google.api.services.manufacturers.v1.ManufacturerCenter;
import com.google.api.services.manufacturers.v1.model.ListProductsResponse;
import com.google.api.services.manufacturers.v1.model.Product;
import java.io.IOException;
import shopping.manufacturers.v1.samples.ManufacturersSample;

/**
 * Sample that gets a list of all of the products for the manufacturer. If there is more than one
 * page of results, we fetch each page in turn.
 */
public class ProductsListSample extends ManufacturersSample {
  public ProductsListSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    ManufacturerCenter.Accounts.Products.List productsList =
        manufacturers.accounts().products().list(getManufacturerId());
    do {
      ListProductsResponse page = productsList.execute();

      if (page.getProducts() == null) {
        System.out.printf("No products found.%n");
        return;
      }
      for (Product product : page.getProducts()) {
        printProduct(product);
      }
      productsList.setPageToken(page.getNextPageToken());
    } while (productsList.getPageToken() != null);
  }

  public static void main(String[] args) throws IOException {
    new ProductsListSample(args).execute();
  }
}
