using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Auth.OAuth2.Flows;
using Google.Apis.Auth.OAuth2.Responses;
using Google.Apis.Http;

namespace ShoppingSamples
{
    internal class Authenticator
    {
        private Authenticator() { }

        public static IConfigurableHttpClientInitializer authenticate(BaseConfig config, string scope)
        {
            String oauthFilePath = Path.Combine(config.ConfigDir, "client-secrets.json");
            String serviceAccountPath = Path.Combine(config.ConfigDir, "service-account.json");
            String[] scopes = new[] { scope };

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
            if (File.Exists(serviceAccountPath))
            {
                Console.WriteLine("Loading service account credentials from " + serviceAccountPath);
                using (FileStream stream = new FileStream(serviceAccountPath, FileMode.Open, FileAccess.Read))
                {
                    GoogleCredential credential = GoogleCredential.FromStream(stream);
                    return credential.CreateScoped(scopes);
                }
            }
            else if (File.Exists(oauthFilePath))
            {
                Console.WriteLine("Loading OAuth2 credentials from " + oauthFilePath);
                using (FileStream oauthFile = File.Open(oauthFilePath, FileMode.Open, FileAccess.Read))
                {
                    var clientSecrets = GoogleClientSecrets.Load(oauthFile).Secrets;
                    if (config.Token != null)
                    {
                        Console.WriteLine("Loading old access token.");
                        config.Token.Scope = scope;
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
                    config.Token.Scope = scope;
                    config.Save();
                    return credential;
                }
            }
            Console.WriteLine("Could not find authentication credentials. Checked:");
            Console.WriteLine(" - Google Application Default Credentials");
            Console.WriteLine(" - " + serviceAccountPath);
            Console.WriteLine(" - " + oauthFilePath);
            Console.WriteLine("Please read the included README for instructions.");
            return null;
        }
    }
}
