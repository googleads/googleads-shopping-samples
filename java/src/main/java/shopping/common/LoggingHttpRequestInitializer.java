package shopping.common;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseInterceptor;
import java.io.IOException;

/**
 * Class that enables logging for requests and responses when used as an HttpRequestInitializer.
 *
 * <p>When constructing an instance, you can optionally provide an existing HttpRequestInitializer
 * that this instance should wrap, so that both request initializers run on all requests and
 * intercept responses as appropriate.
 */
public class LoggingHttpRequestInitializer implements HttpRequestInitializer {
  private final HttpRequestInitializer wrapped;

  public LoggingHttpRequestInitializer() {
    this(null);
  }

  public LoggingHttpRequestInitializer(HttpRequestInitializer toWrap) {
    this.wrapped = toWrap;
  }

  public void initialize(HttpRequest request) throws IOException {
    if (wrapped != null) {
      wrapped.initialize(request);
    }
    request.setLoggingEnabled(true);
    request.setCurlLoggingEnabled(false);
    request.setContentLoggingLimit(Integer.MAX_VALUE);
    request.setResponseInterceptor(
        new HttpResponseInterceptor() {
          private HttpResponseInterceptor wrapped = null;

          public void interceptResponse(HttpResponse response) throws IOException {
            if (wrapped != null) {
              wrapped.interceptResponse(response);
            }
            response.setLoggingEnabled(true);
            response.setContentLoggingLimit(Integer.MAX_VALUE);
          }

          public HttpResponseInterceptor setWrapped(HttpResponseInterceptor toWrap) {
            this.wrapped = toWrap;
            return this;
          }
        }.setWrapped(request.getResponseInterceptor()));
  }
}
