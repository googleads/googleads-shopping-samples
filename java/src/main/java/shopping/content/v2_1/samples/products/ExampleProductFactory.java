package shopping.content.v2_1.samples.products;

import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductShipping;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2_1.samples.ContentConfig;

/**
 * Factory for creating Products to be inserted by the ProductInsert and ProductBatchInsert samples.
 */
public class ExampleProductFactory {
  private static final String CHANNEL = "online";
  private static final String CONTENT_LANGUAGE = "en";
  private static final String TARGET_COUNTRY = "GB";
  private static final String OFFER_ID = "book123";
  private static final int PRODUCT_COUNT = 10;

  public static String sampleProductId() {
    return sampleProductId(OFFER_ID);
  }

  public static String sampleProductId(String offerId) {
    return CHANNEL + ":" + CONTENT_LANGUAGE + ":" + TARGET_COUNTRY + ":" + offerId;
  }

  public static Product create(ContentConfig config) {
    return create(config, OFFER_ID);
  }

  public static Product create(ContentConfig config, String offerId) {
    String websiteUrl = config.getWebsiteUrl();

    if (websiteUrl == null || websiteUrl.equals("")) {
      throw new IllegalStateException(
          "Cannot create example products without a configured website");
    }

    return new Product()
        .setOfferId(offerId)
        .setTitle("A Tale of Two Cities")
        .setDescription("A classic novel about the French Revolution")
        .setLink(websiteUrl + "/tale-of-two-cities.html")
        .setImageLink(websiteUrl + "/tale-of-two-cities.jpg")
        .setChannel(CHANNEL)
        .setContentLanguage(CONTENT_LANGUAGE)
        .setTargetCountry(TARGET_COUNTRY)
        .setAvailability("in stock")
        .setCondition("new")
        .setGoogleProductCategory("Media > Books")
        .setGtin("9780007350896")
        .setPrice(new Price().setValue("2.50").setCurrency("GBP"))
        .setShipping(
            ImmutableList.of(
                new ProductShipping()
                    .setPrice(new Price().setValue("0.99").setCurrency("GBP"))
                    .setCountry("GB")
                    .setService("1st class post")));
  }

  public static ProductsCustomBatchRequest createBatch(ContentConfig config, String prefix) {
    List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries = new ArrayList<>();
    for (int i = 0; i < PRODUCT_COUNT; i++) {
      productsBatchRequestEntries.add(
          new ProductsCustomBatchRequestEntry()
              .setBatchId((long) i)
              .setMerchantId(config.getMerchantId())
              .setProduct(create(config, prefix + i))
              .setMethod("insert"));
    }
    return new ProductsCustomBatchRequest().setEntries(productsBatchRequestEntries);
  }
}
