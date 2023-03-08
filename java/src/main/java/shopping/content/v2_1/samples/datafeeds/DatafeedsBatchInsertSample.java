package shopping.content.v2_1.samples.datafeeds;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.DatafeedsCustomBatchResponse;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that shows batching product inserts. */
public class DatafeedsBatchInsertSample extends ContentSample {
  public DatafeedsBatchInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    try {
      DatafeedsCustomBatchResponse batchResponse =
          content.datafeeds().custombatch(ExampleDatafeedFactory.createBatch(config)).execute();
      DatafeedUtils.printDatafeedBatchResults(batchResponse);
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new DatafeedsBatchInsertSample(args).execute();
  }
}
