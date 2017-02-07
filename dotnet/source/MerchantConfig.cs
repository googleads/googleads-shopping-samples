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
        public new static String CONFIG_DIR = Path.Combine(BaseConfig.CONFIG_DIR, "content");
        private static String CONFIG_FILE = Path.Combine(CONFIG_DIR, "merchant-info.json");

        public override String ConfigDir { get { return CONFIG_DIR; } }
        internal override String ConfigFile { get { return CONFIG_FILE; } }

        [Newtonsoft.Json.JsonPropertyAttribute("merchantId")]
        public ulong MerchantId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("applicationName")]
        public string ApplicationName { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("websiteUrl")]
        public string WebsiteURL { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleUser")]
        public string AccountSampleUser { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("accountSampleAdWordsCID")]
        public ulong AccountSampleAdWordsCID { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("isMCA")]
        public bool IsMCA { get; set; }

        public static MerchantConfig Load()
        {
            if (!File.Exists(CONFIG_FILE))
            {
                Console.WriteLine("Could not find config file at " + MerchantConfig.CONFIG_FILE);
                Console.WriteLine("Please read the included README for instructions.");
            }
            using (StreamReader reader = File.OpenText(CONFIG_FILE))
            {
                return (MerchantConfig)JToken.ReadFrom(new JsonTextReader(reader))
                    .ToObject(typeof(MerchantConfig));
            }
        }

    }
}
