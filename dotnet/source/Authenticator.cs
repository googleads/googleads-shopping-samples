using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Auth.OAuth2.Flows;
using Google.Apis.Auth.OAuth2.Responses;
using Google.Apis.Http;
using Google.Apis.ShoppingContent.v2;

namespace ContentShoppingSamples
{
    internal class Authenticator
    {
        private static String OAUTH_FILE_PATH = Path.Combine(Config.CONFIG_DIR, "content-oauth2.json");
        private static String SERVICE_ACCOUNT_PATH = Path.Combine(Config.CONFIG_DIR, "content-service.json");

        private Authenticator() { }

        public static IConfigurableHttpClientInitializer authenticate(Config config)
        {
            string[] scopes = new[] { ShoppingContentService.Scope.Content };

            try
            {
                GoogleCredential credential = GoogleCredential.GetApplicationDefaultAsync().Result;
                Console.WriteLine("Using Application Default Credentials.");
                return credential.CreateScoped(scopes);
            }
            catch (AggregateException)
            {
                // Do nothing, we'll just let it slide and check the others.
            }
            if (File.Exists(SERVICE_ACCOUNT_PATH))
            {
                Console.WriteLine("Loading service account credentials from " + SERVICE_ACCOUNT_PATH);
                using (FileStream stream = new FileStream(SERVICE_ACCOUNT_PATH, FileMode.Open, FileAccess.Read))
                {
                    GoogleCredential credential = GoogleCredential.FromStream(stream);
                    return credential.CreateScoped(scopes);
                }
            }
            else if (File.Exists(OAUTH_FILE_PATH))
            {
                Console.WriteLine("Loading OAuth2 credentials from " + OAUTH_FILE_PATH);
                using (FileStream oauthFile = File.Open(OAUTH_FILE_PATH, FileMode.Open, FileAccess.Read))
                {
                    var clientSecrets = GoogleClientSecrets.Load(oauthFile).Secrets;
                    if (config.Token != null)
                    {
                        Console.WriteLine("Loading old access token.");
                        config.Token.Scope = ShoppingContentService.Scope.Content;
                        try
                        {
                            var init = new GoogleAuthorizationCodeFlow.Initializer()
                            {
                                ClientSecrets = clientSecrets,
                                Scopes = scopes
                            };
                            var flow = new GoogleAuthorizationCodeFlow(init);
                            UserCredential storedCred =
                                new UserCredential(flow, config.EmailAddress, config.Token);
                            // Want to try and test and make sure we'll actually be able to
                            // use these credentials.
                            if (!storedCred.RefreshTokenAsync(CancellationToken.None).Result)
                            {
                                throw new InvalidDataException();
                            }
                            return storedCred;
                        }
                        catch (InvalidDataException)
                        {
                            Console.WriteLine("Failed to load old access token.");
                            // Ignore, we'll just reauthenticate below.
                        }
                    }
                    UserCredential credential = GoogleWebAuthorizationBroker.AuthorizeAsync(
                        clientSecrets,
                        scopes,
                        config.EmailAddress,
                        CancellationToken.None).Result;
                    config.Token = credential.Token;
                    config.Token.Scope = ShoppingContentService.Scope.Content;
                    config.Save();
                    return credential;
                }
            }
            Console.WriteLine("Could not find authentication credentials. Checked:");
            Console.WriteLine(" - Google Application Default Credentials");
            Console.WriteLine(" - " + SERVICE_ACCOUNT_PATH);
            Console.WriteLine(" - " + OAUTH_FILE_PATH);
            Console.WriteLine("Please read the included README for instructions.");
            return null;
        }
    }
}
