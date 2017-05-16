package shopping.content.v2.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent.Products.List;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsListResponse;
import java.io.IOException;
import shopping.content.v2.samples.ContentSample;

/** Sample demonstrating retrieving only a subset of fields for an item. */
public class PartialResponseSample extends ContentSample {
  public PartialResponseSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    try {
      List productsList = content.products().list(this.config.getMerchantId());
      // Must still select the nextPageToken if you wish to page through results
      productsList.setFields("kind,nextPageToken,resources(id,title)");
      do {
        ProductsListResponse page = productsList.execute();
        for (Product product : page.getResources()) {
          System.out.printf("%s %s%n", product.getId(), product.getTitle());
        }
        if (page.getNextPageToken() == null) {
          break;
        }
        productsList.setPageToken(page.getNextPageToken());
      } while (true);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new PartialResponseSample(args).execute();
  }
}
