using System;
using System.IO;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Auth.OAuth2.Flows;
using Google.Apis.Auth.OAuth2.Responses;
using Google.Apis.Http;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ShoppingSamples
{
    internal class Authenticator
    {
        private Authenticator() { }

        private static readonly string TOKEN_FILE = "stored-token.json";

        public static IConfigurableHttpClientInitializer authenticate(
            BaseConfig config, string scope)
        {
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

            if (config.ConfigDir == null)
            {
                throw new ArgumentException(
                    "Must use Google Application Default Credentials when running without a "
                    + "configuration directory");
            }

            String oauthFilePath = Path.Combine(config.ConfigDir, "client-secrets.json");
            String serviceAccountPath = Path.Combine(config.ConfigDir, "service-account.json");
            if (File.Exists(serviceAccountPath))
            {
                Console.WriteLine("Loading service account credentials from " + serviceAccountPath);
                using (FileStream stream = new FileStream(
                    serviceAccountPath, FileMode.Open, FileAccess.Read))
                {
                    GoogleCredential credential = GoogleCredential.FromStream(stream);
                    return credential.CreateScoped(scopes);
                }
            }
            else if (File.Exists(oauthFilePath))
            {
                Console.WriteLine("Loading OAuth2 credentials from " + oauthFilePath);
                using (FileStream oauthFile =
                    File.Open(oauthFilePath, FileMode.Open, FileAccess.Read))
                {
                    var clientSecrets = GoogleClientSecrets.Load(oauthFile).Secrets;
                    var userId = "unused";
                    TokenResponse token = LoadToken(config, scope);
                    if (token != null)
                    {
                        Console.WriteLine("Loading old access token.");
                        try
                        {
                            var init = new GoogleAuthorizationCodeFlow.Initializer()
                            {
                                ClientSecrets = clientSecrets,
                                Scopes = scopes
                            };
                            var flow = new GoogleAuthorizationCodeFlow(init);
                            UserCredential storedCred = new UserCredential(flow, userId, token);
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
                        clientSecrets, scopes, userId, CancellationToken.None).Result;
                    StoreToken(config, credential.Token);
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

        public static TokenResponse LoadToken(BaseConfig config, string scope)
        {
            var tokenFile = Path.Combine(config.ConfigDir, TOKEN_FILE);
            if (!File.Exists(tokenFile))
            {
                return null;
            }
            using (StreamReader sr = File.OpenText(tokenFile))
            using (JsonTextReader reader = new JsonTextReader(sr))
            {
                JObject json = (JObject)JToken.ReadFrom(reader);
                // Some serializations put an array of scopes into the token, but
                // TokenResponse expects a string, so change appropriately.
                json["scope"] = scope;
                return (TokenResponse)json.ToObject(typeof(TokenResponse));
            }
        }

        public static void StoreToken(BaseConfig config, TokenResponse token)
        {
            var tokenFile = Path.Combine(config.ConfigDir, TOKEN_FILE);
            JsonSerializer serializer = new JsonSerializer();
            using (StreamWriter sw = new StreamWriter(tokenFile))
            using (JsonTextWriter writer = new JsonTextWriter(sw))
            {
                writer.Formatting = Formatting.Indented;
                writer.IndentChar = ' ';
                writer.Indentation = 2;
                serializer.Serialize(writer, token);
            }
        }
    }
}
