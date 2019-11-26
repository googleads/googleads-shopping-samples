package shopping.content.v2_1.samples.datafeeds;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.ShoppingContent.Datafeeds.List;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsListResponse;
import java.io.IOException;
import java.math.BigInteger;
import shopping.content.v2_1.samples.ContentSample;

/**
 * Sample that gets a list of all of the datafeeds for the merchant. If there is more than one page
 * of results, we fetch each page in turn.
 */
public class DatafeedListSample extends ContentSample {
  public DatafeedListSample(String[] args) throws IOException {
    super(args);
  }

  static void listDatafeedsForMerchant(BigInteger merchantId, ShoppingContent content)
      throws IOException {
    List datafeedsList = content.datafeeds().list(merchantId);
    DatafeedsListResponse page = null;

    do {
      if (page != null) {
        datafeedsList.setPageToken(page.getNextPageToken());
      }
      page = datafeedsList.execute();
      if (page.getResources() == null) {
        System.out.println("No datafeeds found.");
        return;
      }
      for (Datafeed datafeed : page.getResources()) {
        DatafeedUtils.printDatafeed(datafeed);
      }
    } while (page.getNextPageToken() != null);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();
    try {
      listDatafeedsForMerchant(config.getMerchantId(), content);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new DatafeedListSample(args).execute();
  }
}
