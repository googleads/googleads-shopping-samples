package shopping.content.v2.samples.datafeeds;

import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedFetchSchedule;
import com.google.api.services.content.model.DatafeedFormat;
import com.google.api.services.content.model.DatafeedsCustomBatchRequest;
import com.google.api.services.content.model.DatafeedsCustomBatchRequestEntry;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2.samples.ContentConfig;

/**
 * Factory for creating Datafeeds to be inserted by the DatafeedInsert and DatafeedsBatchInsert
 * samples.
 */
public class ExampleDatafeedFactory {
  public static final String NAME = "sampleFeed123";
  private static final String CONTENT_LANGUAGE = "en";
  private static final String TARGET_COUNTRY = "GB";
  private static final int DATAFEED_COUNT = 5;

  public static Datafeed create(ContentConfig config) {
    return create(config, NAME);
  }

  public static Datafeed create(ContentConfig config, String name) {
    Datafeed datafeed = new Datafeed();
    String websiteUrl = config.getWebsiteUrl();

    if (websiteUrl == null || websiteUrl.equals("")) {
      throw new IllegalStateException(
          "Cannot create example datafeed without a configured website");
    }

    List<String> destinations = new ArrayList<String>();
    destinations.add("Shopping");

    DatafeedFetchSchedule schedule = new DatafeedFetchSchedule();
    schedule.setWeekday("monday");
    schedule.setHour(6L);
    schedule.setTimeZone("America/Los_Angeles");
    schedule.setFetchUrl(websiteUrl + "/" + name);

    DatafeedFormat format = new DatafeedFormat();
    format.setFileEncoding("utf-8");
    format.setColumnDelimiter("tab");
    format.setQuotingMode("value quoting");

    datafeed.setName(name);
    datafeed.setContentType("products");
    datafeed.setAttributeLanguage("en");
    datafeed.setContentLanguage(CONTENT_LANGUAGE);
    datafeed.setIntendedDestinations(destinations);
    datafeed.setFileName(name);
    datafeed.setTargetCountry(TARGET_COUNTRY);
    datafeed.setFetchSchedule(schedule);
    datafeed.setFormat(format);

    return datafeed;
  }

  public static DatafeedsCustomBatchRequest createBatch(ContentConfig config) {
    return createBatch(config, "sampleFeed");
  }

  public static DatafeedsCustomBatchRequest createBatch(ContentConfig config, String prefix) {
    List<DatafeedsCustomBatchRequestEntry> datafeedsBatchRequestEntries =
        new ArrayList<DatafeedsCustomBatchRequestEntry>();
    for (int i = 0; i < DATAFEED_COUNT; i++) {
      Datafeed datafeed = ExampleDatafeedFactory.create(config, prefix + i);
      datafeedsBatchRequestEntries.add(
          new DatafeedsCustomBatchRequestEntry()
              .setBatchId((long) i)
              .setMerchantId(config.getMerchantId())
              .setDatafeed(datafeed)
              .setMethod("insert"));
    }
    DatafeedsCustomBatchRequest batchRequest = new DatafeedsCustomBatchRequest();
    batchRequest.setEntries(datafeedsBatchRequestEntries);
    return batchRequest;
  }
}
