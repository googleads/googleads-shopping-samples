using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.ManufacturerCenter.v1;
using CommandLine;

namespace ShoppingSamples.Manufacturers
{
    /// <summary>
    /// A sample application that runs multiple requests against the Manufacturer Center API.
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
    internal class ShoppingManufacturerSample
    {
        private static readonly int MaxListPageSize = 50;
        private static readonly string defaultPath = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.Personal),
            "shopping-samples");

        internal class Options {
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

            ManufacturerConfig config = ManufacturerConfig.Load(options.ConfigPath);

            var initializer = Authenticator.authenticate(config, ManufacturerCenterService.Scope.Manufacturercenter);
            if (initializer == null)
            {
                Console.WriteLine("Failed to authenticate, so exiting.");
                return;
            }

            // Create the service.
            var service = new ManufacturerCenterService(new BaseClientService.Initializer()
                {
                    HttpClientInitializer = initializer,
                    ApplicationName = config.ApplicationName,
                });

            ProductsSample productsSample = new ProductsSample(service, MaxListPageSize);

            string manufacturerId = "accounts/" + config.ManufacturerId.ToString();
            productsSample.RunCalls(manufacturerId);
        }
    }
}
