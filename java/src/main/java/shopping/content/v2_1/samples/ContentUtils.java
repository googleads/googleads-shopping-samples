package shopping.content.v2_1.samples;

import com.google.api.services.content.model.Error;
import java.util.List;

/** Utility functions used by samples for multiple services. */
public class ContentUtils {
  public static void printWarnings(List<Error> warnings) {
    printWarnings(warnings, "");
  }

  public static void printWarnings(List<Error> warnings, String prefix) {
    printErrors(warnings, prefix, "warning");
  }

  public static void printErrors(List<Error> errors) {
    printErrors(errors, "", "error");
  }

  public static void printErrors(List<Error> errors, String prefix, String type) {
    if (errors == null) {
      return;
    }
    System.out.printf(prefix + "There are %d %s(s):%n", errors.size(), type);
    for (Error err : errors) {
      System.out.printf(prefix + "- [%s] %s%n", err.getReason(), err.getMessage());
    }
  }
}
