package shopping.content.v2_1.samples.shippingsettings;

import static shopping.common.BaseOption.NO_CONFIG;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.content.ShoppingContent;
import com.google.api.services.content.model.ShippingSettings;
import com.google.api.services.content.model.ShippingsettingsListResponse;
import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import shopping.common.BaseOption;
import shopping.content.v2_1.samples.ContentConfig;
import shopping.content.v2_1.samples.ContentWorkflowSample;

/** Sample that runs through an entire example workflow using the Shippingsettings service. */
public class ShippingsettingsWorkflow extends ContentWorkflowSample {
  private ShippingsettingsWorkflow(
      ShoppingContent content, ShoppingContent sandbox, ContentConfig config) {
    super(content, sandbox, config);
  }

  public static void run(ShoppingContent content, ShoppingContent sandbox, ContentConfig config)
      throws IOException {
    new ShippingsettingsWorkflow(content, sandbox, config).execute();
  }

  @Override
  public void execute() throws IOException {
    System.out.println("---------------------------------");
    System.out.println("Running Shippingsettings service workflow:");
    System.out.println();

    System.out.print("Retrieving original shipping settings...");
    ShippingSettings originalSettings =
        content.shippingsettings().get(config.getMerchantId(), config.getMerchantId()).execute();
    System.out.println("done.");
    System.out.println("Original shipping settings:");
    ShippingsettingsUtils.printShippingSettings(originalSettings);

    System.out.print("Setting new shipping settings...");
    ShippingSettings newSettings = ExampleShippingSettingsFactory.create();
    ShippingSettings response =
        content
            .shippingsettings()
            .update(config.getMerchantId(), config.getMerchantId(), newSettings)
            .execute();
    System.out.println("done.");
    System.out.println("Current shipping settings:");
    ShippingsettingsUtils.printShippingSettings(response);

    System.out.print("Restoring original shipping settings...");
    response =
        content
            .shippingsettings()
            .update(config.getMerchantId(), config.getMerchantId(), originalSettings)
            .execute();
    System.out.println("done.");
    System.out.println("Current shipping settings:");
    ShippingsettingsUtils.printShippingSettings(response);

    if (!config.getIsMCA()) {
      return;
    }

    System.out.println("Listing shipping settings for all sub-accounts:");
    ShoppingContent.Shippingsettings.List shippingSettingsList =
        content.shippingsettings().list(config.getMerchantId());
    do {
      ShippingsettingsListResponse page = shippingSettingsList.execute();
      if (page.getResources() == null) {
        System.out.println("No accounts found.");
        return;
      }
      for (ShippingSettings settings : page.getResources()) {
        ShippingsettingsUtils.printShippingSettings(settings);
      }
      if (page.getNextPageToken() == null) {
        break;
      }
      shippingSettingsList.setPageToken(page.getNextPageToken());
    } while (true);
  }

  public static void main(String[] args) throws IOException {
    CommandLine parsedArgs = BaseOption.parseOptions(args);
    File configPath = null;
    if (!NO_CONFIG.isSet(parsedArgs)) {
      configPath = BaseOption.checkedConfigPath(parsedArgs);
    }
    ContentConfig config = ContentConfig.load(configPath);

    ShoppingContent.Builder builder = createStandardBuilder(parsedArgs, config);
    ShoppingContent content = createService(builder);
    ShoppingContent sandbox = createSandboxContentService(builder);
    retrieveConfiguration(content, config);

    try {
      new ShippingsettingsWorkflow(content, sandbox, config).execute();
    } catch (GoogleJsonResponseException e) {
      checkGoogleJsonResponseException(e);
    }
  }
}
