import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * Sample that demonstrates fetching OAuth2 authentication tokens.
 */
public class GetRefreshTokenSample {
  private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
  private static final List<String> SCOPES =
      Collections.singletonList("https://www.googleapis.com/auth/content");

  private static Config config;
  private static HttpTransport httpTransport;
  private static JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  public static void main(String[] args) throws GeneralSecurityException, IOException {
    config = Config.load();
    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleAuthorizationCodeRequestUrl url =
        new GoogleAuthorizationCodeRequestUrl(config.getClientId(), REDIRECT_URI, SCOPES);
    url.setAccessType("offline");

    System.out.println("Visit the following URL and log in:");
    System.out.println();
    System.out.println("\t" + url.toString());
    System.out.println();
    System.out.print("Then type the resulting code here: ");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String code = reader.readLine();

    GoogleAuthorizationCodeTokenRequest request =
        new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory, config.getClientId(),
            config.getClientSecret(), code, REDIRECT_URI);
    GoogleTokenResponse response = request.execute();

    System.out.println("Now open '.shopping-content-samples.json' in your home directory and"
        + " enter the following in the refresh token field:");
    System.out.println();
    System.out.println("\t" + response.getRefreshToken());
  }
}
