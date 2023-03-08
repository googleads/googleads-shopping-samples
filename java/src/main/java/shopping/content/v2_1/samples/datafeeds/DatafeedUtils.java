package shopping.content.v2_1.samples.datafeeds;

import com.google.api.services.content.model.Datafeed;
import com.google.api.services.content.model.DatafeedsCustomBatchRequest;
import com.google.api.services.content.model.DatafeedsCustomBatchRequestEntry;
import com.google.api.services.content.model.DatafeedsCustomBatchResponse;
import com.google.api.services.content.model.DatafeedsCustomBatchResponseEntry;
import shopping.content.v2_1.samples.ContentUtils;

/** Utility class for methods like printing Datafeed objects. */
public class DatafeedUtils {
  public static void printDatafeed(Datafeed datafeed) {
    if (datafeed.getId() != null) {
      System.out.printf("Datafeed with name %s and ID %d%n", datafeed.getName(), datafeed.getId());
    } else {
      System.out.printf("Datafeed with name %s%n", datafeed.getName());
    }
  }

  public static void printDatafeedBatchRequest(DatafeedsCustomBatchRequest request) {
    if (request.getEntries() == null || request.getEntries().isEmpty()) {
      System.out.println("No entries in custombatch request.");
    }
    for (DatafeedsCustomBatchRequestEntry entry : request.getEntries()) {
      if (entry.getBatchId() == null) {
        throw new IllegalArgumentException("No batch ID in entry");
      }
      if (entry.getMerchantId() == null) {
        throw new IllegalArgumentException(
            String.format("No merchant ID in batch entry %s", entry.getBatchId()));
      }
      if (entry.getMethod().equals("insert")) {
        if (entry.getDatafeed() == null) {
          throw new IllegalArgumentException(
              String.format(
                  "No datafeed configuration in insert batch entry %s", entry.getBatchId()));
        }
        System.out.printf("Batch entry %s, insert succeeded:%n", entry.getBatchId());
        printDatafeed(entry.getDatafeed());
      } else if (entry.getMethod().equals("update")) {
        if (entry.getDatafeed() == null) {
          throw new IllegalArgumentException(
              String.format(
                  "No datafeed configuration in update batch entry %s", entry.getBatchId()));
        }
        System.out.printf("Batch entry %s, update succeeded:%n", entry.getBatchId());
        printDatafeed(entry.getDatafeed());
      } else if (entry.getMethod().equals("get")) {
        if (entry.getDatafeedId() == null) {
          throw new IllegalArgumentException(
              String.format("No datafeed ID in get batch entry %s", entry.getBatchId()));
        }
        System.out.printf(
            "Batch entry %s: get datafeed %s%n", entry.getBatchId(), entry.getDatafeedId());
      } else if (entry.getMethod().equals("delete")) {
        if (entry.getDatafeedId() == null) {
          throw new IllegalArgumentException(
              String.format("No datafeed ID in delete batch entry %s", entry.getBatchId()));
        }
        System.out.printf(
            "Batch entry %s: delete datafeed %s%n", entry.getBatchId(), entry.getDatafeedId());
      } else {
        throw new IllegalArgumentException(
            String.format(
                "Unknown method in batch entry %s: %s", entry.getBatchId(), entry.getMethod()));
      }
    }
  }

  public static void printDatafeedBatchResults(DatafeedsCustomBatchResponse response) {
    if (response.getEntries() == null) {
      System.out.println("There were no results from the batch request.");
      return;
    }
    for (DatafeedsCustomBatchResponseEntry entry : response.getEntries()) {
      if (entry.getErrors() != null) {
        System.out.printf("Errors in batch entry %d:%n", entry.getBatchId());
        ContentUtils.printErrors(entry.getErrors().getErrors());
      } else {
        System.out.printf("Successfully executed batch entry %d%n", entry.getBatchId());
        if (entry.getDatafeed() != null) {
          printDatafeed(entry.getDatafeed());
        }
      }
    }
  }
}
