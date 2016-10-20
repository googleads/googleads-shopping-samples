package shopping.v2.samples.products;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.model.Error;
import com.google.api.services.content.model.Product;

import java.io.IOException;
import java.util.List;
import shopping.v2.samples.BaseSample;

/**
 * Sample that inserts a product. The product created here is used in other samples.
 */
public class ProductInsertSample extends BaseSample {
  public ProductInsertSample() throws IOException {}

  @Override
  public void execute() throws IOException {
    // Create a product with ID 'online:en:GB:book123'
    Product product = ExampleProductFactory.create("online", "en", "GB", "book123");

    try {
      Product result = content.products().insert(merchantId, product).execute();
      List<Error> warnings = result.getWarnings();

      System.out.printf("There are %d warning(s)%n", warnings.size());

      for (Error warning : warnings) {
        System.out.printf("[%s] %s%n", warning.getReason(), warning.getMessage());
      }
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
    new ProductInsertSample().execute();
  }
}
