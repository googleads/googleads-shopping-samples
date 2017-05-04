package shopping.common;

import static shopping.common.BaseOption.CONFIG_PATH;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Base class for both sets of API samples.
 */
public abstract class BaseSample {
  protected final Credential credential;
  protected final HttpTransport httpTransport;
  protected final Authenticator authenticator;
  protected final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

  public BaseSample(String[] args) throws IOException, ParseException {
    Options options = BaseOption.createCommandLineOptions();
    CommandLineParser parser = new DefaultParser();
    CommandLine parsedArgs = parser.parse(options, args);
    if (parsedArgs.hasOption("h")) {
      printHelpAndExit(options);
    }
    loadConfig(convertConfigPath(parsedArgs));
    httpTransport = createHttpTransport();
    authenticator = loadAuthentication();
    credential = createCredential();
  }

  protected void printHelpAndExit(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("samples", options, true);
    System.exit(0);
  }

  protected File convertConfigPath(CommandLine line) throws IOException {
    String pathString = CONFIG_PATH.getOptionValue(line);
    File path = new File(pathString);
    if (!path.exists()) {
      throw new IOException("Configuration directory '" + pathString + "' does not exist");
    }
    return path;
  }

  protected HttpTransport createHttpTransport() throws IOException {
    try {
      return GoogleNetHttpTransport.newTrustedTransport();
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  protected Credential createCredential() throws IOException {
    return authenticator.authenticate();
  }

  protected void checkGoogleJsonResponseException(GoogleJsonResponseException e)
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

  protected abstract void loadConfig(File configPath) throws IOException;
  protected abstract Authenticator loadAuthentication() throws IOException;
  public abstract void execute() throws IOException;
}
