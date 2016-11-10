package shopping.v2.samples.datafeeds;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsCustomBatchRequest;
import com.google.api.services.content.model.DatafeedsCustomBatchRequestEntry;
import com.google.api.services.content.model.DatafeedsCustomBatchResponse;
import com.google.api.services.content.model.DatafeedsCustomBatchResponseEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import shopping.v2.samples.BaseSample;

/**
 * Sample that shows batching product inserts.
 */
public class DatafeedsBatchInsertSample extends BaseSample {
  private static final int DATAFEED_COUNT = 5;

  public DatafeedsBatchInsertSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    try {
      List<DatafeedsCustomBatchRequestEntry> datafeedsBatchRequestEntries =
          new ArrayList<DatafeedsCustomBatchRequestEntry>();
      DatafeedsCustomBatchRequest batchRequest = new DatafeedsCustomBatchRequest();
      for (int i = 0; i < DATAFEED_COUNT; i++) {
        // Create a datafeed with name 'sampleFeed{i}'
        Datafeed datafeed = ExampleDatafeedFactory.create(config, "en", "GB", "sampleFeed" + i);
        DatafeedsCustomBatchRequestEntry entry = new DatafeedsCustomBatchRequestEntry();
        entry.setBatchId((long) i);
        entry.setMerchantId(this.config.getMerchantId());
        entry.setDatafeed(datafeed);
        entry.setMethod("insert");
        datafeedsBatchRequestEntries.add(entry);
      }
      batchRequest.setEntries(datafeedsBatchRequestEntries);
      DatafeedsCustomBatchResponse batchResponse =
          content.datafeeds().custombatch(batchRequest).execute();

      for (DatafeedsCustomBatchResponseEntry entry : batchResponse.getEntries()) {
        if (entry.getErrors() != null) {
          System.out.printf("Errors in batch entry %d:%n", entry.getBatchId());
          printErrors(entry.getErrors().getErrors());
        } else {
          Datafeed datafeed = entry.getDatafeed();
          System.out.printf("Inserted datafeed %s with ID %d%n",
              datafeed.getName(), datafeed.getId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new DatafeedsBatchInsertSample().execute();
  }
}
