package shopping.v2.samples.datafeeds;

import com.google.api.services.content.ShoppingContent.Datafeeds.List;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsListResponse;
import java.io.IOException;
import shopping.v2.samples.BaseSample;

/**
 * Sample that gets a list of all of the datafeeds for the merchant. If there is more than one page
 * of results, we fetch each page in turn.
 */
public class DatafeedListSample extends BaseSample {
  public DatafeedListSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    List datafeedsList = content.datafeeds().list(this.config.getMerchantId());
    do {
      DatafeedsListResponse page = datafeedsList.execute();
      for (Datafeed datafeed : page.getResources()) {
        System.out.printf("Datafeed with name %s and ID %d%n", datafeed.getName(),
            datafeed.getId());
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      datafeedsList.setPageToken(page.getNextPageToken());
    } while (true);
  }

  public static void main(String[] args) throws IOException {
    new DatafeedListSample().execute();
  }
}
