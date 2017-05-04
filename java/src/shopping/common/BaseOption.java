package shopping.common;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/** Base command line options supported by all samples. */
public enum BaseOption {
  CONFIG_PATH(
      "p",
      "config_path",
      "PATH",
      "Configuration directory for Shopping samples",
      new File(System.getProperty("user.home"), "shopping-samples").getAbsolutePath()),
  HELP("h", "help", "print this message"),
  ROOT_URL("r", "root_url", "URL", "Root URL for API calls if non-standard", null);

  private final String option;
  private final String longOpt;
  private final String description;
  private final boolean hasArg;
  private final String argName;
  private final String defaultArg;

  private BaseOption(String option, String longOpt, String description,
      boolean hasArg, String argName, String defaultArg) {
    this.option = option;
    this.longOpt = longOpt;
    this.description = description;
    this.hasArg = hasArg;
    this.argName = argName;
    this.defaultArg = defaultArg;
  }

  private BaseOption(String option, String longOpt, String description,
      String argName, String defaultArg) {
    this(option, longOpt, description, true, argName, defaultArg);
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
}
