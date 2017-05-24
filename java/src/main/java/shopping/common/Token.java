package shopping.common;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Wrapper to convert between JSON representation of tokens used in config file and
 * StoredCredentials used by OAuth2 library.
 */
public class Token extends GenericJson {
  @Key("access_token")
  private String accessToken;

  @Key("refresh_token")
  private String refreshToken;

  @Key("expiration_time_millis")
  private Long expirationTimeMilliseconds;

  public static Token fromStoredCredential(StoredCredential credential) {
    Token token = new Token();
    token.setAccessToken(credential.getAccessToken());
    token.setRefreshToken(credential.getRefreshToken());
    token.setExpirationTimeMilliseconds(credential.getExpirationTimeMilliseconds());
    return token;
  }

  public StoredCredential toStoredCredential() {
    StoredCredential credential = new StoredCredential();
    credential.setAccessToken(this.getAccessToken());
    credential.setRefreshToken(this.getRefreshToken());
    credential.setExpirationTimeMilliseconds(this.getExpirationTimeMilliseconds());
    return credential;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Long getExpirationTimeMilliseconds() {
    return expirationTimeMilliseconds;
  }

  public void setExpirationTimeMilliseconds(Long expirationTimeMilliseconds) {
    this.expirationTimeMilliseconds = expirationTimeMilliseconds;
  }
}
