// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package shopping.merchant.samples.utils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.util.Key;
import com.google.auth.oauth2.ClientId;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserAuthorizer;
import com.google.auth.oauth2.UserCredentials;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that contains all the authentication logic, both for service accounts and to create an
 * OAuth 2 refresh token for the Merchant API.
 *
 * <p>IMPORTANT FOR OAUTH: For web app clients types, you must add {@code http://127.0.0.1} to the
 * "Authorized redirect URIs" list in your Google Cloud Console project before running this example.
 * Desktop app client types do not require the local redirect to be explicitly configured in the
 * console.
 *
 * <p>This example will start a basic server that listens for requests at {@code
 * http://127.0.0.1:PORT}, where {@code PORT} is dynamically assigned.
 */
public class Authenticator {

  // OAUTH2_CALLBACK_BASE_URI set to localhost by default
  private static final String OAUTH2_CALLBACK_BASE_URI = "http://127.0.0.1";
  // Scopes for the generated OAuth2 credentials. The list here only contains the Merchant API
  // scope, but you can add multiple scopes if you want to use the credentials for other Google
  // APIs.
  private static final ImmutableList<String> SCOPES =
      ImmutableList.<String>builder().add("https://www.googleapis.com/auth/content").build();

  public GoogleCredentials authenticate() throws IOException {
    Config config = Config.load();
    if (config.getPath() == null) {
      throw new IllegalArgumentException(
          "Must update Config.java to set a configuration directory.");
    }
    File serviceAccountFile = new File(config.getPath(), "service-account.json");
    System.out.printf("Checking for service account file at: %s%n", serviceAccountFile);
    if (serviceAccountFile.exists()) {
      System.out.println("Attempting to load service account credentials");
      try (InputStream inputStream = new FileInputStream(serviceAccountFile)) {
        GoogleCredentials credential = GoogleCredentials.fromStream(inputStream);
        System.out.println("Successfully loaded service account credentials");
        return credential;
      }
    }

    System.out.println("No service account file found.");
    // Non-service account OAuth flow below
    // First see if a refresh token exists, and if so, use it
    File tokenFile = new File(config.getPath(), "token.json");
    System.out.printf("Checking for user credentials file at: %s%n", tokenFile);
    if (tokenFile.exists()) {
      System.out.println("Loading OAuth2 refresh token.");
      UserCredentials userCredentials = UserCredentials.fromStream(new FileInputStream(tokenFile));
      System.out.println("Successfully loaded OAuth2 refresh token");
      return userCredentials;
    }

    // If the refresh token does not exist, attempt to use client
    // credentials to get a refresh token
    File clientSecretsFile = new File(config.getPath(), "client-secrets.json");
    if (!clientSecretsFile.exists()) {
      throw new IOException(
        "No authentication credentials found. Checked the paths "
            + serviceAccountFile.getCanonicalPath()
            + " and "
            + clientSecretsFile.getCanonicalPath()
            + ". Please read the accompanying README.");
    }
      
    System.out.println("Loading OAuth2 client credentials.");
    try (InputStream inputStream = new FileInputStream(clientSecretsFile)) {
      ClientId parsedClient = ClientId.fromStream(inputStream);
      String clientId = parsedClient.getClientId();
      String clientSecret = parsedClient.getClientSecret();
      // Creates an anti-forgery state token as described here:
      // https://developers.google.com/identity/protocols/OpenIDConnect#createxsrftoken
      String state = new BigInteger(130, new SecureRandom()).toString(32);

      // Creates an HTTP server that will listen for the OAuth2 callback request.
      URI baseUri;
      UserAuthorizer userAuthorizer;
      AuthorizationResponse authorizationResponse = null;

      try (SimpleCallbackServer simpleCallbackServer = new SimpleCallbackServer()) {
        userAuthorizer =
            UserAuthorizer.newBuilder()
                .setClientId(ClientId.of(clientId, clientSecret))
                .setScopes(SCOPES)
                // Provides an empty callback URI so that no additional suffix is added to the
                // redirect. By default, UserAuthorizer will use "/oauth2callback" if this is
                // either
                // not set or set to null.
                .setCallbackUri(URI.create(""))
                .build();
        baseUri =
            URI.create(OAUTH2_CALLBACK_BASE_URI + ":" + simpleCallbackServer.getLocalPort());
        System.out.printf(
            "Paste this url in your browser:%n%s%n",
            userAuthorizer.getAuthorizationUrl("", state, baseUri));

        // Waits for the authorization code.
        simpleCallbackServer.accept();
        authorizationResponse = simpleCallbackServer.authorizationResponse;
      }

      if (authorizationResponse == null || authorizationResponse.code == null) {
        throw new NullPointerException(
            "OAuth2 callback did not contain an authorization code: " + authorizationResponse);
      }

      // Confirms that the state in the response matches the state token used to generate the
      // authorization URL.
      if (!state.equals(authorizationResponse.state)) {
        throw new IllegalStateException("State does not match expected state");
      }

      // Exchanges the authorization code for credentials and print the refresh token.
      UserCredentials userCredentials =
          userAuthorizer.getCredentialsFromCode(authorizationResponse.code, baseUri);
      System.out.printf("Your new refresh token is: %s%n", userCredentials.getRefreshToken());

      // Save the refresh token to be used for the future
      userCredentials.save(new File(config.getPath(), "token.json").getPath());

      return userCredentials;
    } catch (IOException e) {
      throw new IOException(
          "Could not retrieve OAuth2 client credentials from the file "
              + clientSecretsFile.getCanonicalPath());
    }
  }

  /** Basic server that listens for the OAuth2 callback. */
  private static class SimpleCallbackServer extends ServerSocket {

    private AuthorizationResponse authorizationResponse;

    SimpleCallbackServer() throws IOException {
      // Passes a port # of zero so that a port will be automatically allocated.
      super(0);
    }

    /**
     * Blocks until a connection is made to this server. After this method completes, the
     * authorizationResponse of this server will be set, provided the request line is in the
     * expected format.
     */
    @Override
    public Socket accept() throws IOException {
      Socket socket = super.accept();

      try (BufferedReader in =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
        String callbackRequest = in.readLine();
        // Uses a regular expression to extract the request line from the first line of the
        // callback request, e.g.:
        //   GET /?code=AUTH_CODE&state=XYZ&scope=https://www.googleapis.com/auth/adwords HTTP/1.1
        Pattern pattern = Pattern.compile("GET +([^ ]+)");
        Matcher matcher = pattern.matcher(Strings.nullToEmpty(callbackRequest));
        if (matcher.find()) {
          String relativeUrl = matcher.group(1);
          authorizationResponse = new AuthorizationResponse(OAUTH2_CALLBACK_BASE_URI + relativeUrl);
        }
        try (Writer outputWriter = new OutputStreamWriter(socket.getOutputStream())) {
          outputWriter.append("HTTP/1.1 ");
          outputWriter.append(Integer.toString(HttpStatusCodes.STATUS_CODE_OK));
          outputWriter.append(" OK\n");
          outputWriter.append("Content-Type: text/html\n\n");

          outputWriter.append("<b>");
          if (authorizationResponse.code != null) {
            outputWriter.append("Authorization code was successfully retrieved.");
          } else {
            outputWriter.append("Failed to retrieve authorization code.");
          }
          outputWriter.append("</b>");
          outputWriter.append("<p>Please check the console output from <code>");
          outputWriter.append(Authenticator.class.getSimpleName());
          outputWriter.append("</code> for further instructions.");
        }
      }
      return socket;
    }
  }

  /** Response object with attributes corresponding to OAuth2 callback parameters. */
  static class AuthorizationResponse extends GenericUrl {

    /** The authorization code to exchange for an access token and (optionally) a refresh token. */
    @Key String code;

    /** Error from the request or from the processing of the request. */
    @Key String error;

    /** State parameter from the callback request. */
    @Key String state;

    /**
     * Constructs a new instance based on an absolute URL. All fields annotated with the {@link Key}
     * annotation will be set if they are present in the URL.
     *
     * @param encodedUrl absolute URL with query parameters.
     */
    public AuthorizationResponse(String encodedUrl) {
      super(encodedUrl);
    }

    // @Override
    public String toString() {
      return MoreObjects.toStringHelper(getClass())
          .add("code", code)
          .add("error", error)
          .add("state", state)
          .toString();
    }
  }
}
