package shopping.v2.samples;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.services.content.ShoppingContentScopes;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class that contains all the authentication logic, including choosing between
 * service account credentials and OAuth2 client credentials.
 */
public class Authenticator {

  private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

  private static final File SERVICE_ACCOUNT_FILE = new File(Config.CONFIG_DIR,
      "content-service.json");
  private static final File CLIENT_SECRETS_FILE = new File(Config.CONFIG_DIR,
      "content-oauth2.json");

  private HttpTransport httpTransport;
  private JsonFactory jsonFactory;
  private Config config;
  private DataStoreFactory dataStoreFactory;

  public Authenticator(HttpTransport httpTransport, JsonFactory jsonFactory, Config config)
      throws IOException {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.config = config;
    this.dataStoreFactory = new ConfigDataStoreFactory(config);
  }

  protected Credential authenticate() throws IOException {
    InputStream inputStream = null;

    try {
      Credential credential = GoogleCredential.getApplicationDefault()
          .createScoped(ShoppingContentScopes.all());
      System.out.println("Loaded the Application Default Credentials.");
      return credential;
    } catch (IOException e) {
      // No need to do anything, we'll fall back on other credentials.
    }
    if (SERVICE_ACCOUNT_FILE.exists()) {
      System.out.println("Loading service account credentials.");
      try {
        inputStream = new FileInputStream(SERVICE_ACCOUNT_FILE);
        GoogleCredential credential =
          GoogleCredential.fromStream(inputStream, httpTransport, jsonFactory)
              .createScoped(ShoppingContentScopes.all());
        System.out.printf("Loaded service account credentials for %s%n",
            credential.getServiceAccountId());
        return credential;
      } catch (IOException e) {
        throw new IOException("Could not retrieve service account credentials from the file "
            + SERVICE_ACCOUNT_FILE.getCanonicalPath());
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    if (CLIENT_SECRETS_FILE.exists()) {
      System.out.println("Loading OAuth2 client credentials.");
      try {
        inputStream = new FileInputStream(CLIENT_SECRETS_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
            new InputStreamReader(inputStream));
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            httpTransport, jsonFactory, clientSecrets, ShoppingContentScopes.all())
            .setDataStoreFactory(dataStoreFactory)
            .build();
        // authorize
        String userID = config.getEmailAddress();
        Credential storedCredential = flow.loadCredential(userID);;
        if (storedCredential != null) {
          System.out.printf("Retrieved stored credential for user %s%n", userID);
          return storedCredential;
        }
        LocalServerReceiver receiver =
            new LocalServerReceiver.Builder().setHost("localhost").setPort(9999).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userID);
        System.out.printf("Retrieved credential for user %s from web%n", userID);
        return credential;
      } catch (IOException e) {
        throw new IOException("Could not retrieve OAuth2 client credentials from the file "
            + CLIENT_SECRETS_FILE.getCanonicalPath());
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    throw new IOException("No authentication credentials found. Checked the Google Application"
        + "Default Credentials and the paths "
        + SERVICE_ACCOUNT_FILE.getCanonicalPath() + " and " + CLIENT_SECRETS_FILE.getCanonicalPath()
        + ". Please read the accompanying README.");
  }
}