using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.ShoppingContent.v2;
using CommandLine;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample application that runs multiple requests against the Content API for Shopping.
    /// <list type="bullet">
    /// <item>
    /// <description>Initializes the user credentials</description>
    /// </item>
    /// <item>
    /// <description>Creates the service that queries the API</description>
    /// </item>
    /// <item>
    /// <description>Executes the requests</description>
    /// </item>
    /// </list>
    /// </summary>
    internal class ShoppingContentSample
    {
        private static readonly int MaxListPageSize = 50;
        private static readonly string defaultPath = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.Personal),
            "shopping-samples");

        internal class Options {
            // Would like to use the DefaultValue attribute here, but we have to calculate the
            // default path at runtime, which means it can't be used as an attribute value.
            [Option('p', "config_path",
                HelpText = "Configuration directory for Shopping samples.")]
            public string ConfigPath { get; set; }
        }

        [STAThread]
        internal static void Main(string[] args)
        {
            Console.WriteLine("Content API for Shopping Command Line Sample");
            Console.WriteLine("============================================");

            var options = new Options();
            CommandLine.Parser.Default.ParseArgumentsStrict(args, options);

            if (options.ConfigPath == null)
            {
                options.ConfigPath = defaultPath;
            }

            MerchantConfig config = MerchantConfig.Load(options.ConfigPath);

            var initializer = Authenticator.authenticate(config, ShoppingContentService.Scope.Content);
            if (initializer == null)
            {
                Console.WriteLine("Failed to authenticate, so exiting.");
                return;
            }

            // Create the service.
            var service = new ShoppingContentService(new BaseClientService.Initializer()
                {
                    HttpClientInitializer = initializer,
                    ApplicationName = config.ApplicationName,
                });

            AccountsSample accountsSample = new AccountsSample(service);
            AccountstatusesSample accountstatusesSample =
                new AccountstatusesSample(service, MaxListPageSize);
            AccounttaxSample accounttaxSample = new AccounttaxSample(service);
            DatafeedsSample datafeedsSample = new DatafeedsSample(service);
            ProductsSample productsSample = new ProductsSample(service, MaxListPageSize);
            ProductstatusesSample productstatusesSample =
                new ProductstatusesSample(service, MaxListPageSize);
            ShippingsettingsSample shippingsettingsSample = new ShippingsettingsSample(service);
            MultiClientAccountSample multiClientAccountSample = new MultiClientAccountSample(service);

            if (!config.IsMCA)
            {
                // Non-MCA calls
                productsSample.RunCalls(config.MerchantId, config.WebsiteURL);
                productstatusesSample.RunCalls(config.MerchantId);
                datafeedsSample.RunCalls(config.MerchantId);
                accountstatusesSample.RunCalls(config.MerchantId);
                accountsSample.RunCalls(config.MerchantId, config.AccountSampleUser, config.AccountSampleAdWordsCID);
                accounttaxSample.RunCalls(config.MerchantId);
                shippingsettingsSample.RunCalls(config.MerchantId);
            }
            else
            {
                // MCA calls
                accountstatusesSample.RunMultiCalls(config.MerchantId);
                multiClientAccountSample.RunCalls(config.MerchantId);
            }
        }
    }
}
