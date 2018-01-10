package shopping.content.v2.samples.datafeeds;

import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedFetchSchedule;
import com.google.api.services.content.model.DatafeedFormat;
import com.google.api.services.content.model.DatafeedsCustomBatchRequest;
import com.google.api.services.content.model.DatafeedsCustomBatchRequestEntry;
import com.google.common.collect.ImmutableList;
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
    String websiteUrl = config.getWebsiteUrl();

    if (websiteUrl == null || websiteUrl.equals("")) {
      throw new IllegalStateException(
          "Cannot create example datafeed without a configured website");
    }

    return new Datafeed()
        .setName(name)
        .setContentType("products")
        .setAttributeLanguage("en")
        .setContentLanguage(CONTENT_LANGUAGE)
        .setIntendedDestinations(ImmutableList.of("Shopping"))
        .setFileName(name)
        .setTargetCountry(TARGET_COUNTRY)
        .setFetchSchedule(
            new DatafeedFetchSchedule()
                .setWeekday("monday")
                .setHour(6L)
                .setTimeZone("America/Los_Angeles")
                .setFetchUrl(websiteUrl + "/" + name))
        .setFormat(
            new DatafeedFormat()
                .setFileEncoding("utf-8")
                .setColumnDelimiter("tab")
                .setQuotingMode("value quoting"));
  }

  public static DatafeedsCustomBatchRequest createBatch(ContentConfig config) {
    return createBatch(config, "sampleFeed");
  }

  public static DatafeedsCustomBatchRequest createBatch(ContentConfig config, String prefix) {
    List<DatafeedsCustomBatchRequestEntry> datafeedsBatchRequestEntries = new ArrayList<>();
    for (int i = 0; i < DATAFEED_COUNT; i++) {
      datafeedsBatchRequestEntries.add(
          new DatafeedsCustomBatchRequestEntry()
              .setBatchId((long) i)
              .setMerchantId(config.getMerchantId())
              .setDatafeed(create(config, prefix + i))
              .setMethod("insert"));
    }
    return new DatafeedsCustomBatchRequest().setEntries(datafeedsBatchRequestEntries);
  }
}
