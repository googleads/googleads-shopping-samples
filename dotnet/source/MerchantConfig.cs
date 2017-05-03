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
        public override String ConfigDir { get; set; }
        internal override String ConfigFile { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("merchantId")]
        public ulong MerchantId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("websiteUrl")]
        public string WebsiteURL { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleUser")]
        public string AccountSampleUser { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleAdWordsCID")]
        public ulong AccountSampleAdWordsCID { get; set; }

        // Set by retrieving MCA status via the API after service setup in
        // ShoppingContentSample.Main().
        public bool IsMCA { get; set; }

        public static MerchantConfig Load(String configPath)
        {
            MerchantConfig config;
            var contentPath = Path.Combine(configPath, "content");
            if (!Directory.Exists(contentPath))
            {
                Console.WriteLine("Could not find configuration directory at " + contentPath);
                Console.WriteLine("Please read the included README for instructions.");
                throw new FileNotFoundException("Missing configuration directory");
            }

            var contentFile = Path.Combine(contentPath, "merchant-info.json");
            if (!File.Exists(contentFile))
            {
                Console.WriteLine("Could not find configuration file at " + contentFile);
                Console.WriteLine("Please read the included README for instructions.");
                throw new FileNotFoundException("Missing configuration file");
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
