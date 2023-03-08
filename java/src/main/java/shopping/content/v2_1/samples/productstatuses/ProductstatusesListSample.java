package shopping.content.v2_1.samples.productstatuses;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.ProductStatus;
import com.google.api.services.content.model.ProductstatusesListResponse;
import java.io.IOException;
import java.math.BigInteger;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample that gets the status of each of the products for the merchant. If there is more than one
 * page of results, we fetch each page in turn.
 */
public class ProductstatusesListSample extends ContentSample {
  public ProductstatusesListSample(String[] args) throws IOException {
    super(args);
  }

  static void listProductStatusesForMerchant(BigInteger merchantId, ShoppingContent content)
      throws IOException {
    ShoppingContent.Productstatuses.List productStatusesList =
        content.productstatuses().list(merchantId);

    ProductstatusesListResponse page = null;

    do {
      if (page != null) {
        productStatusesList.setPageToken(page.getNextPageToken());
      }
      page = productStatusesList.execute();
      if (page.getResources() == null) {
        System.out.println("No products found.");
        return;
      }
      for (ProductStatus productStatus : page.getResources()) {
        ProductstatusUtils.printProductStatus(productStatus);
      }
    } while (page.getNextPageToken() != null);
  }

  @Override
  public void execute() throws IOException {
    try {
      listProductStatusesForMerchant(config.getMerchantId(), content);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductstatusesListSample(args).execute();
  }
}
