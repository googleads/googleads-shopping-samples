import com.google.api.services.content.model.Error;
import com.google.api.services.content.model.Product;

import java.io.IOException;
import java.util.List;

/**
 * Sample that inserts a product. The product created here is used in other samples.
 */
public class ProductInsertSample extends BaseSample {
  @Override
  public void execute() throws IOException {
    // Create a product with ID 'online:en:GB:book123'
    Product product = ExampleProductFactory.create("online", "en", "GB", "book123");
    Product result = content.products().insert(merchantId, product).execute();
    List<Error> warnings = result.getWarnings();

    System.out.printf("There are %d warnings%n", warnings.size());

    for (Error warning : warnings) {
      System.out.printf("[%s] %s%n", warning.getReason(), warning.getMessage());
    }
  }

  public static void main(String[] args) throws IOException {
    new ProductInsertSample().execute();
  }
}
