using System;
using System.IO;
using Google.Apis.Auth.OAuth2.Responses;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A data class for storing the contents of merchant-info.json.
    /// </summary>
    internal class MerchantConfig : BaseConfig
    {
        public override string ConfigDir { get; set; }
        internal override string ConfigFile { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("merchantId")]
        public ulong? MerchantId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleUser")]
        public string AccountSampleUser { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleGoogleAdsCID")]
        public ulong? AccountSampleGoogleAdsCID { get; set; }

        // The following fields are retrieved via the API after service setup in
        // BaseContentSample#initializeService().
        public bool IsMCA { get; set; }
        public string WebsiteURL { get; set; }

        public static MerchantConfig Load(String configPath)
        {
            MerchantConfig config;
            var contentPath = Path.Combine(configPath, "content");
            if (!Directory.Exists(contentPath))
            {
                Console.WriteLine($"Could not find configuration directory at {contentPath}");
                Console.WriteLine("Please read the included README for instructions.");
                throw new FileNotFoundException("Missing configuration directory");
            }

            var contentFile = Path.Combine(contentPath, "merchant-info.json");
            if (!File.Exists(contentFile))
            {
                Console.WriteLine($"No configuration file at {contentFile}");
                Console.WriteLine("Assuming default configuration for authenticated user.");
                config = new MerchantConfig {
                  ConfigDir = contentPath
                };
                return config;
            }
            using (StreamReader reader = File.OpenText(contentFile))
            {
                config = (MerchantConfig)JToken.ReadFrom(new JsonTextReader(reader))
                    .ToObject(typeof(MerchantConfig));
                config.ConfigDir = contentPath;
                config.ConfigFile = contentFile;
            }

            return config;
        }

    }
}
