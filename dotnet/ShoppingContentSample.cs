using System;
using System.Text;
using System.Threading;

using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;

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

        private static String CLIENT_ID = "INSERT_CLIENT_ID_HERE";
        private static String CLIENT_SECRET = "INSERT_CLIENT_SECRET_HERE";

        // INSERT MERCHANT ID
        private static ulong MERCHANT_ID = 1234567890;

        // INSERT EMAIL ADDRESS to modify Accounts.Users. Leave empty to ignore these calls.
        private static String EMAIL_ADDRESS = "";

        // INSERT ADWORDS ACCOUNT ID. 0 to ignore these calls.
        private static ulong ADWORDS_ACCOUNT_ID = 0;

        // INSERT MULTI-ACCOUNT MERCHANT ID. 0 to ignore these calls.
        // Note that, if set, everything else is ignored.
        private static ulong MCA_MERCHANT_ID = 0;

        [STAThread]
        internal static void Main(string[] args)
        {
            Console.WriteLine("Content API for Shopping Command Line Sample");
            Console.WriteLine("============================================");

            GoogleWebAuthorizationBroker.Folder = "ShoppingContent.Sample";
            var credential = GoogleWebAuthorizationBroker.AuthorizeAsync(
                new ClientSecrets
                {
                    ClientId = CLIENT_ID,
                    ClientSecret = CLIENT_SECRET
                },
                new string[] { ShoppingContentService.Scope.Content},
                "user",
                CancellationToken.None).Result;

            // Create the service.
            var service = new ShoppingContentService(new BaseClientService.Initializer()
            {
                HttpClientInitializer = credential,
                ApplicationName = "Shopping Content Sample",

            });

            // Execute the calls.
            ShoppingContentApiConsumer shoppingContentApiConsumer =
                new ShoppingContentApiConsumer(service, MaxListPageSize);


            if (MCA_MERCHANT_ID == 0)
            {
                // Non-MCA calls
                shoppingContentApiConsumer.RunCalls(MERCHANT_ID, EMAIL_ADDRESS, ADWORDS_ACCOUNT_ID);
            }
            else
            {
                // MCA calls
                shoppingContentApiConsumer.RunMultiClientAccountCalls(MCA_MERCHANT_ID);
            }

            Console.WriteLine("Press any key to continue...");
            Console.ReadKey();
        }
    }
}
