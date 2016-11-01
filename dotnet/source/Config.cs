using System;
using System.IO;
using Google.Apis.Auth.OAuth2.Responses;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ContentShoppingSamples
{
    /// <summary>
    /// A data class for storing the contents of merchant-info.json.
    /// </summary>
    internal class Config
    {
        public static String CONFIG_DIR =
            Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.Personal), ".shopping-content-samples");
        private static String CONFIG_FILE = Path.Combine(CONFIG_DIR, "merchant-info.json");

        [Newtonsoft.Json.JsonPropertyAttribute("merchantId")]
        public ulong MerchantId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("applicationName")]
        public string ApplicationName { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("emailAddress")]
        public string EmailAddress { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("websiteUrl")]
        public string WebsiteURL { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleUser")]
        public string AccountSampleUser { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleAdWordsCID")]
        public ulong AccountSampleAdWordsCID { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("isMCA")]
        public bool IsMCA { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("token")]
        public TokenResponse Token { get; set; }

        public static Config Load()
        {
            if (!File.Exists(CONFIG_FILE))
            {
                Console.WriteLine("Could not find config file at " + Config.CONFIG_FILE);
                Console.WriteLine("Please read the included README for instructions.");
            }
            using (StreamReader reader = File.OpenText(CONFIG_FILE))
            {
                return (Config)JToken.ReadFrom(new JsonTextReader(reader))
                                     .ToObject(typeof(Config));
            }
        }

        public void Save()
        {
            JsonSerializer serializer = new JsonSerializer();
            using (StreamWriter sw = new StreamWriter(CONFIG_FILE))
            using (JsonTextWriter writer = new JsonTextWriter(sw))
            {
                writer.Formatting = Formatting.Indented;
                writer.IndentChar = ' ';
                writer.Indentation = 2;
                serializer.Serialize(writer, this);
            }
        }
    }
}
