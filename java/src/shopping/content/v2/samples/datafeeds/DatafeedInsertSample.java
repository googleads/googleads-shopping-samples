package shopping.content.v2.samples.datafeeds;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Datafeed;
import java.io.IOException;
import shopping.content.v2.samples.ContentSample;

/** Sample that inserts a datafeed. The datafeed created here is used in other samples. */
public class DatafeedInsertSample extends ContentSample {
  public DatafeedInsertSample(String[] args) throws IOException {
    super(args);
  }

  @Override
  public void execute() throws IOException {
    checkNonMCA();

    // Create a datafeed with name sampleFeed123
    String name = "sampleFeed123";
    Datafeed datafeed = ExampleDatafeedFactory.create(config, "en", "GB", name);

    try {
      Datafeed result = content.datafeeds().insert(this.config.getMerchantId(), datafeed).execute();
      System.out.printf("Datafeed %s inserted with ID %d\n", name, result.getId());
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError err = e.getDetails();
      if (err.getCode() >= 400 && err.getCode() < 500) {
        System.out.printf("There are %d error(s)%n", err.getErrors().size());
        for (ErrorInfo info : err.getErrors()) {
          System.out.printf("- [%s] %s%n", info.getReason(), info.getMessage());
        }
      } else {
        throw e;
      }
    }
  }

  public static void main(String[] args) throws IOException {
    new DatafeedInsertSample(args).execute();
  }
}
