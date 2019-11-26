package shopping.content.v2_1.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent.Products.List;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductsListResponse;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

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

      ProductsListResponse page = null;

      do {
        if (page != null) {
          productsList.setPageToken(page.getNextPageToken());
        }
        page = productsList.execute();
        for (Product product : page.getResources()) {
          System.out.printf("%s %s%n", product.getId(), product.getTitle());
        }
      } while (page.getNextPageToken() != null);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new PartialResponseSample(args).execute();
  }
}
