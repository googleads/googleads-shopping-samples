package shopping.common;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Class that contains all the authentication logic, including choosing between service account
 * credentials and OAuth2 client credentials.
 */
public class Authenticator {

  private Set<String> scopes;
  private Config config;
  private HttpTransport httpTransport;
  private JsonFactory jsonFactory;
  private DataStoreFactory dataStoreFactory;

  public Authenticator(
      HttpTransport httpTransport, JsonFactory jsonFactory, Set<String> scopes, Config config)
      throws IOException {
    this.scopes = scopes;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.config = config;
    this.dataStoreFactory = new ConfigDataStoreFactory(config);
  }

  public Credential authenticate() throws IOException {
    try {
      Credential credential = GoogleCredential.getApplicationDefault().createScoped(scopes);
      System.out.println("Loaded the Application Default Credentials.");
      return credential;
    } catch (IOException e) {
      // No need to do anything, we'll fall back on other credentials.
    }
    if (config.getPath() == null) {
      throw new IllegalArgumentException(
          "Must use Application Default Credentials with no configuration directory.");
    }
    File serviceAccountFile = new File(config.getPath(), "service-account.json");
    if (serviceAccountFile.exists()) {
      System.out.println("Loading service account credentials.");
      try (InputStream inputStream = new FileInputStream(serviceAccountFile)) {
        GoogleCredential credential =
            GoogleCredential.fromStream(inputStream, httpTransport, jsonFactory)
                .createScoped(scopes);
        System.out.printf(
            "Loaded service account credentials for %s%n", credential.getServiceAccountId());
        // GoogleCredential.fromStream does NOT refresh, since scopes must be added after.
        if (!credential.refreshToken()) {
          System.out.println("The service account access token could not be refreshed.");
          System.out.println("The service account key may have been deleted in the API Console.");
          throw new IOException("Failed to refresh service account token");
        }
        return credential;
      } catch (IOException e) {
        throw new IOException(
            "Could not retrieve service account credentials from the file "
                + serviceAccountFile.getCanonicalPath(), e);
      }
    }
    File clientSecretsFile = new File(config.getPath(), "client-secrets.json");
    if (clientSecretsFile.exists()) {
      System.out.println("Loading OAuth2 client credentials.");
      try (InputStream inputStream = new FileInputStream(clientSecretsFile)) {
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        String userID = ConfigDataStoreFactory.UNUSED_ID;
        Credential storedCredential = flow.loadCredential(userID);
        if (storedCredential != null) {
          System.out.printf("Retrieved stored credential for %s from cache.%n", userID);
          return storedCredential;
        }
        LocalServerReceiver receiver =
            new LocalServerReceiver.Builder().setHost("localhost").setPort(9999).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize(userID);
        System.out.printf("Retrieved credential for %s from web.%n", userID);
        return credential;
      } catch (IOException e) {
        throw new IOException(
            "Could not retrieve OAuth2 client credentials from the file "
                + clientSecretsFile.getCanonicalPath());
      }
    }
    throw new IOException(
        "No authentication credentials found. Checked the Google Application"
            + "Default Credentials and the paths "
            + serviceAccountFile.getCanonicalPath()
            + " and "
            + clientSecretsFile.getCanonicalPath()
            + ". Please read the accompanying README.");
  }
}
