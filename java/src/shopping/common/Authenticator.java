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

  private File serviceAccountFile;
  private File clientSecretsFile;
  private Set<String> scopes;
  private Config config;
  private HttpTransport httpTransport;
  private JsonFactory jsonFactory;
  private DataStoreFactory dataStoreFactory;

  public Authenticator(
      HttpTransport httpTransport, JsonFactory jsonFactory, Set<String> scopes, Config config)
      throws IOException {
    this.serviceAccountFile = new File(config.getPath(), "service-account.json");
    this.clientSecretsFile = new File(config.getPath(), "client-secrets.json");
    this.scopes = scopes;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.config = config;
    this.dataStoreFactory = new ConfigDataStoreFactory(config);
  }

  public Credential authenticate() throws IOException {
    InputStream inputStream = null;

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
    if (serviceAccountFile.exists()) {
      System.out.println("Loading service account credentials.");
      try {
        inputStream = new FileInputStream(serviceAccountFile);
        GoogleCredential credential =
            GoogleCredential.fromStream(inputStream, httpTransport, jsonFactory)
                .createScoped(scopes);
        System.out.printf(
            "Loaded service account credentials for %s%n", credential.getServiceAccountId());
        return credential;
      } catch (IOException e) {
        throw new IOException(
            "Could not retrieve service account credentials from the file "
                + serviceAccountFile.getCanonicalPath());
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
      }
    }
    if (clientSecretsFile.exists()) {
      System.out.println("Loading OAuth2 client credentials.");
      try {
        inputStream = new FileInputStream(clientSecretsFile);
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(jsonFactory, new InputStreamReader(inputStream));
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        String userID = config.getEmailAddress();
        Credential storedCredential = flow.loadCredential(userID);
        ;
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
        throw new IOException(
            "Could not retrieve OAuth2 client credentials from the file "
                + clientSecretsFile.getCanonicalPath());
      } finally {
        if (inputStream != null) {
          inputStream.close();
        }
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
