package shopping.content.v2_1.samples.datafeeds;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsCustomBatchRequest;
import com.google.api.services.content.model.DatafeedsCustomBatchRequestEntry;
import com.google.api.services.content.model.DatafeedsCustomBatchResponse;
import com.google.api.services.content.model.DatafeedsCustomBatchResponseEntry;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/** Sample that runs through an entire example workflow using the Accountstatuses service. */
public class DatafeedsWorkflow extends ContentWorkflowSample {
  private DatafeedsWorkflow(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new DatafeedsWorkflow(content, sandbox, config).execute();
  }

  private DatafeedsCustomBatchRequest deleteBatch(DatafeedsCustomBatchResponse batchResponse) {
    List<DatafeedsCustomBatchRequestEntry> datafeedsBatchRequestEntries = new ArrayList<>();
    for (DatafeedsCustomBatchResponseEntry e : batchResponse.getEntries()) {
      if (e.getDatafeed() != null) {
        datafeedsBatchRequestEntries.add(
            new DatafeedsCustomBatchRequestEntry()
                .setBatchId(e.getBatchId())
                .setMerchantId(config.getMerchantId())
                .setDatafeedId(BigInteger.valueOf(e.getDatafeed().getId()))
                .setMethod("delete"));
      }
    }
    return new DatafeedsCustomBatchRequest().setEntries(datafeedsBatchRequestEntries);
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");

    if (config.getIsMCA()) {
      System.out.println(
          "The Merchant Center account is an MCA, so not running the Datafeeds workflow.");
      return;
    }

    System.out.println("Running Datafeeds service workflow:");
    System.out.println();

    Random rand = new Random();
    String baseName = "samples" + rand.nextInt(5000);

    System.out.println("Listing current datafeeds:");
    DatafeedListSample.listDatafeedsForMerchant(config.getMerchantId(), content);

    System.out.println("Creating new datafeed:");
    Datafeed datafeed = ExampleDatafeedFactory.create(config, baseName);
    Datafeed newFeed = content.datafeeds().insert(config.getMerchantId(), datafeed).execute();
    System.out.printf("Datafeed %s created with ID %d\n", newFeed.getName(), newFeed.getId());

    BigInteger feedId = BigInteger.valueOf(newFeed.getId());
    System.out.println("Retrieving new datafeed:");
    Datafeed response = content.datafeeds().get(config.getMerchantId(), feedId).execute();
    DatafeedUtils.printDatafeed(response);

    System.out.println("Creating multiple datafeeds via batch:");
    DatafeedsCustomBatchRequest insertRequest =
        ExampleDatafeedFactory.createBatch(config, baseName + "_");
    DatafeedUtils.printDatafeedBatchRequest(insertRequest);
    DatafeedsCustomBatchResponse batchResponse =
        content.datafeeds().custombatch(insertRequest).execute();
    DatafeedUtils.printDatafeedBatchResults(batchResponse);

    System.out.println("Listing current datafeeds:");
    DatafeedListSample.listDatafeedsForMerchant(config.getMerchantId(), content);

    System.out.println("Deleting new datafeeds:");
    content.datafeeds().delete(config.getMerchantId(), feedId).execute();
    System.out.printf("Datafeed %s deleted.%n", feedId);
    DatafeedsCustomBatchRequest deleteRequest = deleteBatch(batchResponse);
    DatafeedUtils.printDatafeedBatchRequest(deleteRequest);
    DatafeedsCustomBatchResponse batchDeleteResponse =
        content.datafeeds().custombatch(deleteRequest).execute();
    DatafeedUtils.printDatafeedBatchResults(batchDeleteResponse);

    System.out.println("Listing current datafeeds:");
    DatafeedListSample.listDatafeedsForMerchant(config.getMerchantId(), content);
  }

  public static void main(String[] args) throws IOException {
    CommandLine parsedArgs = BaseOption.parseOptions(args);
    File configPath = null;
    if (!NO_CONFIG.isSet(parsedArgs)) {
      configPath = BaseOption.checkedConfigPath(parsedArgs);
    }
    ContentConfig config = ContentConfig.load(configPath);

    ShoppingContent.Builder builder = createStandardBuilder(parsedArgs, config);
    ShoppingContent content = createService(builder);
    ShoppingContent sandbox = createSandboxContentService(builder);
    retrieveConfiguration(content, config);

    try {
      new DatafeedsWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
