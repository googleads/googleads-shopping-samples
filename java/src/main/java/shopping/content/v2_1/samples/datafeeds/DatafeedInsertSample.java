package shopping.content.v2_1.samples.datafeeds;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Datafeed;
import java.io.IOException;
import shopping.content.v2_1.samples.ContentSample;

/** Sample that inserts a datafeed. The datafeed created here is used in other samples. */
public class DatafeedInsertSample extends ContentSample {
  public DatafeedInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    try {
      Datafeed datafeed = ExampleDatafeedFactory.create(config);
      Datafeed result = content.datafeeds().insert(this.config.getMerchantId(), datafeed).execute();
      System.out.printf("Datafeed %s inserted with ID %d\n", result.getName(), result.getId());
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    new DatafeedInsertSample(args).execute();
  }
}
