package shopping.common;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.BackOffUtils;
import com.google.api.client.util.Sleeper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** Base class for both sets of API workflow samples. */
public abstract class BaseWorkflowSample {
  protected static final String ENDPOINT_ENV_VAR = "GOOGLE_SHOPPING_SAMPLES_ENDPOINT";

  protected static void checkGoogleJsonResponseException(GoogleJsonResponseException e)
      throws GoogleJsonResponseException {
    GoogleJsonError err = e.getDetails();
    // err can be null if response is not JSON
    if (err != null) {
      // For errors in the 4xx range, print out the errors and continue normally.
      if (err.getCode() >= 400 && err.getCode() < 500) {
        System.out.printf("There are %d error(s)%n", err.getErrors().size());
        for (ErrorInfo info : err.getErrors()) {
          System.out.printf("- [%s] %s%n", info.getReason(), info.getMessage());
        }
      } else {
        throw e;
      }
    } else {
      throw e;
    }
  }

  protected static <T extends AbstractGoogleClient> T createService(
      AbstractGoogleClient.Builder builder) {
    String endpoint = System.getenv(ENDPOINT_ENV_VAR);
    if (endpoint != null) {
      try {
        URI u = new URI(endpoint);
        if (!u.isAbsolute()) {
          throw new IllegalArgumentException("Endpoint URL must be absolute: " + endpoint);
        }
        builder.setRootUrl(u.resolve("/").toString());
        builder.setServicePath(u.getPath());
        System.out.println("Using non-standard API endpoint: " + endpoint);
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
    @SuppressWarnings({"unchecked"})
    T built = (T) builder.build();
    return built;
  }

  protected <T extends GenericJson> T retryFailures(
      AbstractGoogleClientRequest<T> request, BackOff backOff) throws IOException {
    while (true) {
      try {
        return request.execute();
      } catch (GoogleJsonResponseException e) {
        try {
          long nextPause = backOff.nextBackOffMillis();
          if (nextPause == BackOff.STOP) {
            throw e;
          }
          System.out.printf("Operation failed, retrying in %f seconds.%n", nextPause / 1000.0);
          BackOffUtils.next(Sleeper.DEFAULT, backOff);
        } catch (InterruptedException ie) {
          // Just go straight into retry if interrupted.
        }
      }
    }
  }

  public abstract void execute() throws IOException;
}
