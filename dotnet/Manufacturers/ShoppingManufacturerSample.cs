using System;
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
    internal class ShoppingManufacturerSample : BaseSample
    {
        private static readonly int MaxListPageSize = 50;

        // BasePath/BaseUri do not expose setters, so we subclass ManufacturerCenterService
        // so we can override their values.
        private class ManufacturerCenterServiceWithBaseUri : ManufacturerCenterService
        {
            private string baseUri;
            private string basePath;

            public override string BaseUri { get { return this.baseUri; } }
            public override string BasePath { get { return this.basePath; } }

            public ManufacturerCenterServiceWithBaseUri(
                BaseClientService.Initializer init, Uri u)
                : base(init)
            {
                this.baseUri = u.AbsoluteUri;
                this.basePath = u.AbsolutePath;
            }
        }

        private ManufacturerConfig config;
        private ManufacturerCenterService service;

        internal override BaseConfig Config { get { return config; } }
        internal override string Scope
        {
            get { return ManufacturerCenterService.Scope.Manufacturercenter; }
        }
        internal override string ApiName { get { return "Manufacturer Center API"; } }
        internal override IClientService Service { get { return service; } }

        internal override void initializeConfig(bool noconfig)
        {
            if (noconfig == true)
            {
                throw new ArgumentException(
                    "Cannot run Manufacturer Center API Samples without a configuration.");
            }
            config = ManufacturerConfig.Load(CliOptions.ConfigPath);
        }

        internal override void initializeService(BaseClientService.Initializer init)
        {
            service = new ManufacturerCenterService(init);
        }

        internal override void initializeService(BaseClientService.Initializer init, Uri u)
        {
            service = new ManufacturerCenterServiceWithBaseUri(init, u);
        }

        internal override void runCalls()
        {
            ProductsSample productsSample = new ProductsSample(service, MaxListPageSize);

            string manufacturerId = "accounts/" + config.ManufacturerId.ToString();
            productsSample.RunCalls(manufacturerId);
        }

        [STAThread]
        internal static void Main(string[] args)
        {
            var samples = new ShoppingManufacturerSample();
            samples.startSamples(args);
        }
    }
}
