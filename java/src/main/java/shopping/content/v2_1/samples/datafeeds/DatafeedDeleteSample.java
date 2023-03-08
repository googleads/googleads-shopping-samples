package shopping.content.v2_1.samples.datafeeds;

import com.google.api.services.content.ShoppingContent.Datafeeds.List;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsListResponse;
import java.io.IOException;
import java.math.BigInteger;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample of deleting a product. It will delete the product that is created by the ProductInsert
 * sample.
 */
public class DatafeedDeleteSample extends ContentSample {
  public DatafeedDeleteSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    // We can't know what ID the datafeed we created has, so first search for it using
    // the list method, then delete it.
    String name = ExampleDatafeedFactory.NAME;
    BigInteger id = null;

    List datafeedsList = content.datafeeds().list(this.config.getMerchantId());
    DatafeedsListResponse page = null;

    do {
      if (page != null) {
        datafeedsList.setPageToken(page.getNextPageToken());
      }
      page = datafeedsList.execute();
      for (Datafeed datafeed : page.getResources()) {
        if (datafeed.getName().equals(name)) {
          id = BigInteger.valueOf(datafeed.getId());
          break;
        }
      }
    } while (page.getNextPageToken() != null);

    if (id == null) {
      System.out.println("Sample datafeed not found. Run DatafeedInsertSample first.");
      return;
    }

    content.datafeeds().delete(this.config.getMerchantId(), id).execute();

    System.out.printf("Datafeed %s with id %d deleted.\n", name, id);
  }

  public static void main(String[] args) throws IOException {
    new DatafeedDeleteSample(args).execute();
  }
}
