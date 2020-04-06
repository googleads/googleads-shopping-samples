package shopping.common;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/** Base command line options supported by all samples. */
public enum BaseOption {
  CONFIG_PATH(
      "p",
      "config_path",
      "PATH",
      "Configuration directory for Shopping samples",
      new File(System.getProperty("user.home"), "shopping-samples").getAbsolutePath()),
  CALL_LOG_FILE("l", "log_file", "FILE", "File for logging API requests and responses"),
  NO_CONFIG("n", "noconfig", "Run samples without a configuration directory"),
  HELP("h", "help", "print this message");

  private final String option;
  private final String longOpt;
  private final String description;
  private final boolean hasArg;
  private final String argName;
  private final String defaultArg;

  private BaseOption(
      String option,
      String longOpt,
      String description,
      boolean hasArg,
      String argName,
      String defaultArg) {
    this.option = option;
    this.longOpt = longOpt;
    this.description = description;
    this.hasArg = hasArg;
    this.argName = argName;
    this.defaultArg = defaultArg;
  }

  private BaseOption(
      String option, String longOpt, String description, String argName, String defaultArg) {
    this(option, longOpt, description, true, argName, defaultArg);
  }

  private BaseOption(String option, String longOpt, String argName, String description) {
    this(option, longOpt, description, true, argName, null);
  }

  private BaseOption(String option, String longOpt, String description) {
    this(option, longOpt, description, false, null, null);
  }

  public String getOptionValue(CommandLine cmdLine) {
    if (cmdLine.hasOption(option)) {
      return cmdLine.getOptionValue(option);
    } else {
      return this.defaultArg;
    }
  }

  public boolean isSet(CommandLine cmdLine) {
    return cmdLine.hasOption(option);
  }

  /**
   * Creates the command line options.
   *
   * @return the {@link Options}
   */
  public static Options createCommandLineOptions() {
    Options options = new Options();

    for (BaseOption option : BaseOption.values()) {
      options.addOption(
          Option.builder(option.option)
              .required(false)
              .hasArg(option.hasArg)
              .argName(option.argName)
              .longOpt(option.longOpt)
              .desc(option.description)
              .build());
    }

    return options;
  }

  public static CommandLine parseOptions(String[] args) {
    Options options = createCommandLineOptions();
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine parsedArgs = parser.parse(options, args);
      if (parsedArgs.hasOption("h")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("samples", options, true);
        System.exit(0);
      }
      return parsedArgs;
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static File checkedConfigPath(CommandLine parsedArgs) {
    String pathString = CONFIG_PATH.getOptionValue(parsedArgs);
    File path = new File(pathString);
    if (!path.exists()) {
      throw new IllegalArgumentException(
          "Configuration directory '" + pathString + "' does not exist");
    }
    return path;
  }

  public static HttpRequestInitializer installLogging(
      HttpRequestInitializer initializer, CommandLine parsedArgs) throws IOException {
    String filename = CALL_LOG_FILE.getOptionValue(parsedArgs);
    if (filename == null) {
      return initializer;
    }
    Logger logger = Logger.getLogger(HttpTransport.class.getName());
    logger.setLevel(Level.CONFIG);
    Handler handler = new FileHandler(filename);
    // Only output log messages so that the log files are simply just the requests and responses
    // in text form. (SimpleFormatter would output timestamps/logger names for each log call.)
    handler.setFormatter(
        new Formatter() {
          @Override
          public String format(LogRecord record) {
            return formatMessage(record);
          }
        });
    handler.setLevel(Level.CONFIG);
    logger.addHandler(handler);
    return new LoggingHttpRequestInitializer(initializer);
  }

  public static HttpRequestInitializer increaseTimeout(
      HttpRequestInitializer initializer) throws IOException {
        return new TimeoutHttpRequestInitializer(initializer);
  }
}
