package shopping.content.v2.samples.datafeeds;

import com.google.api.services.content.ShoppingContent.Datafeeds.List;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsListResponse;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import shopping.content.v2.samples.ContentSample;

/**
 * Sample that gets a list of all of the datafeeds for the merchant. If there is more than one page
 * of results, we fetch each page in turn.
 */
public class DatafeedListSample extends ContentSample {
  public DatafeedListSample(String[] args) throws IOException, ParseException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    List datafeedsList = content.datafeeds().list(this.config.getMerchantId());
    do {
      DatafeedsListResponse page = datafeedsList.execute();
      if (page.getResources() == null) {
        System.out.println("No datafeeds found.");
        return;
      }
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

  public static void main(String[] args) throws IOException, ParseException {
    new DatafeedListSample(args).execute();
  }
}
