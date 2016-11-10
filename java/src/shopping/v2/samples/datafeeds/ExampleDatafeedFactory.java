package shopping.v2.samples.datafeeds;

import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedFetchSchedule;
import com.google.api.services.content.model.DatafeedFormat;
import java.util.ArrayList;
import java.util.List;
import shopping.v2.samples.Config;

/**
 * Factory for creating Datafeeds to be inserted by the DatafeedInsert and DatafeedsBatchInsert
 * samples.
 */
public class ExampleDatafeedFactory {
  public static Datafeed create(Config config, String contentLanguage, String targetCountry,
      String name) {
    Datafeed datafeed = new Datafeed();
    String websiteUrl = config.getWebsiteUrl();

    if (config.getWebsiteUrl() == null) {
      websiteUrl = "http://feeds.my-shop.com";
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
    datafeed.setContentLanguage(contentLanguage);
    datafeed.setIntendedDestinations(destinations);
    datafeed.setFileName(name);
    datafeed.setTargetCountry(targetCountry);
    datafeed.setFetchSchedule(schedule);
    datafeed.setFormat(format);

    return datafeed;
  }
}
