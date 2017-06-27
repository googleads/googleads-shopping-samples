package shopping.content.v2.samples.products;

import com.google.api.services.content.model.Price;
import com.google.api.services.content.model.Product;
import com.google.api.services.content.model.ProductShipping;
import com.google.api.services.content.model.ProductsCustomBatchRequest;
import com.google.api.services.content.model.ProductsCustomBatchRequestEntry;
import java.util.ArrayList;
import java.util.List;
import shopping.content.v2.samples.ContentConfig;

/**
 * Factory for creating Products to be inserted by the ProductInsert and ProductBatchInsert samples.
 */
public class ExampleProductFactory {
  private static final String CHANNEL = "online";
  private static final String CONTENT_LANGUAGE = "en";
  private static final String TARGET_COUNTRY = "GB";
  private static final int PRODUCT_COUNT = 10;

  public static Product create(ContentConfig config, String offerId) {
    Product product = new Product();
    String websiteUrl = config.getWebsiteUrl();

    if (websiteUrl == null || websiteUrl.equals("")) {
      throw new IllegalStateException(
          "Cannot create example products without a configured website");
    }

    product.setOfferId(offerId);
    product.setTitle("A Tale of Two Cities");
    product.setDescription("A classic novel about the French Revolution");
    product.setLink(websiteUrl + "/tale-of-two-cities.html");
    product.setImageLink(websiteUrl + "/tale-of-two-cities.jpg");
    product.setChannel(CHANNEL);
    product.setContentLanguage(CONTENT_LANGUAGE);
    product.setTargetCountry(TARGET_COUNTRY);
    product.setAvailability("in stock");
    product.setCondition("new");
    product.setGoogleProductCategory("Media > Books");
    product.setGtin("9780007350896");

    Price price = new Price();
    price.setValue("2.50");
    price.setCurrency("GBP");
    product.setPrice(price);

    Price shippingPrice = new Price();
    shippingPrice.setValue("0.99");
    shippingPrice.setCurrency("GBP");

    ProductShipping shipping = new ProductShipping();
    shipping.setPrice(shippingPrice);
    shipping.setCountry("GB");
    shipping.setService("1st class post");

    ArrayList<ProductShipping> shippingList = new ArrayList<ProductShipping>();
    shippingList.add(shipping);
    product.setShipping(shippingList);

    return product;
  }

  public static ProductsCustomBatchRequest createBatch(ContentConfig config, String prefix) {
    List<ProductsCustomBatchRequestEntry> productsBatchRequestEntries =
        new ArrayList<ProductsCustomBatchRequestEntry>();
    for (int i = 0; i < PRODUCT_COUNT; i++) {
      Product product = ExampleProductFactory.create(config, prefix + i);
      productsBatchRequestEntries.add(
          new ProductsCustomBatchRequestEntry()
              .setBatchId((long) i)
              .setMerchantId(config.getMerchantId())
              .setProduct(product)
              .setMethod("insert"));
    }
    ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
    batchRequest.setEntries(productsBatchRequestEntries);
    return batchRequest;
  }
}
