package shopping.common;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import java.io.IOException;

/**
 * Class that enables logging for requests and responses when used as an HttpRequestInitializer.
 *
 * <p>When constructing an instance, you can optionally provide an existing HttpRequestInitializer
 * that this instance should wrap, so that both request initializers run on all requests and
 * intercept responses as appropriate.
 */
public class TimeoutHttpRequestInitializer implements HttpRequestInitializer {
  private final HttpRequestInitializer wrapped;

  public TimeoutHttpRequestInitializer() {
    this(null);
  }

  public TimeoutHttpRequestInitializer(HttpRequestInitializer toWrap) {
    this.wrapped = toWrap;
  }

  public void initialize(HttpRequest request) throws IOException {
    if (wrapped != null) {
      wrapped.initialize(request);
    }
    request.setConnectTimeout(1 * 60000);  // Raises default timeout to 60 seconds from 20 seconds
    request.setReadTimeout(1 * 60000);  // Raises default timeout to 60 seconds from 20 seconds
  }
}
