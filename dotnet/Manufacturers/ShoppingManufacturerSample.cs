using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.ManufacturerCenter.v1;

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

        [STAThread]
        internal static void Main(string[] args)
        {
            Console.WriteLine("Content API for Shopping Command Line Sample");
            Console.WriteLine("============================================");

            ManufacturerConfig config = ManufacturerConfig.Load();

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
