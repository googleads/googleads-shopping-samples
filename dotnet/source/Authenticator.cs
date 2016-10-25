using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.ShoppingContent.v2;

namespace ContentShoppingSamples
{
    internal class Authenticator
    {
        private static String OAUTH_FILE_PATH = Path.Combine(Config.CONFIG_DIR, "content-oauth2.json");
        private static String SERVICE_ACCOUNT_PATH = Path.Combine(Config.CONFIG_DIR, "content-service.json");

        private Authenticator() { }

        public static ICredential authenticate(Config config)
        {
            ICredential credential;
            if (File.Exists(SERVICE_ACCOUNT_PATH))
            {
                using (FileStream stream = new FileStream(SERVICE_ACCOUNT_PATH, FileMode.Open, FileAccess.Read))
                {
                    // Cast is needed for 1.10.0 auth libraries (e.g. .NET 4.0), not
                    // latest ones (.NET 4.5 compatible).
                    credential = (Google.Apis.Auth.OAuth2.ICredential)GoogleCredential.FromStream(stream);
                }
            }
            else if (File.Exists(OAUTH_FILE_PATH))
            {
                var oauthFile = File.Open(OAUTH_FILE_PATH, FileMode.Open, FileAccess.Read);
                var clientSecrets = GoogleClientSecrets.Load(oauthFile);
                GoogleWebAuthorizationBroker.Folder = "ShoppingContent.Sample";
                credential = GoogleWebAuthorizationBroker.AuthorizeAsync(
                    clientSecrets.Secrets,
                    new string[] { ShoppingContentService.Scope.Content },
                    config.EmailAddress,
                    CancellationToken.None).Result;
            }
            else
            {
                Console.WriteLine("Could not find authentication credentials. Checked:");
                Console.WriteLine(" - " + SERVICE_ACCOUNT_PATH);
                Console.WriteLine(" - " + OAUTH_FILE_PATH);
                Console.WriteLine("Please read the included README for instructions.");
                credential = null;
            }
            return credential;
        }
    }
}
