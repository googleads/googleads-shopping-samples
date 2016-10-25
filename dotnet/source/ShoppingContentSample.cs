using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.ShoppingContent.v2;

namespace ContentShoppingSamples
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

        [STAThread]
        internal static void Main(string[] args)
        {
            Console.WriteLine("Content API for Shopping Command Line Sample");
            Console.WriteLine("============================================");

            Config config = Config.Load();

            ICredential credential = Authenticator.authenticate(config);

            // Create the service.
            var service = new ShoppingContentService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credential,
                ApplicationName = config.ApplicationName,
            });

            // Execute the calls, first getting the website from the account information..
            AccountsSample accountsSample = new AccountsSample(service);
            DatafeedsSample datafeedsSample = new DatafeedsSample(service);
            ProductsSample productsSample = new ProductsSample(service, MaxListPageSize);
            MultiClientAccountSample multiClientAccountSample = new MultiClientAccountSample(service);
            if (!config.IsMCA)
            {
                // Non-MCA calls
                // Get the website from the Merchant Center account so we can avoid errors in created products.
                string websiteUrl = accountsSample.GetWebsiteUrl(config.MerchantId);
                productsSample.RunCalls(config.MerchantId, websiteUrl);
                datafeedsSample.RunCalls(config.MerchantId);
                accountsSample.RunCalls(config.MerchantId, config.AccountSampleUser, config.AccountSampleAdWordsCID);
            }
            else
            {
                // MCA calls
                multiClientAccountSample.RunCalls(config.MerchantId);
            }
        }
    }
}
