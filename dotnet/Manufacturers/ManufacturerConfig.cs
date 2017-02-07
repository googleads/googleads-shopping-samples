using System;
using System.IO;
using Google.Apis.Auth.OAuth2.Responses;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ShoppingSamples.Manufacturers
{
    /// <summary>
    /// A data class for storing the contents of manufacturer-info.json.
    /// </summary>
    internal class ManufacturerConfig : BaseConfig
    {
        public new static String CONFIG_DIR = Path.Combine(BaseConfig.CONFIG_DIR, "manufacturers");
        private static String CONFIG_FILE = Path.Combine(CONFIG_DIR, "manufacturer-info.json");

        public override String ConfigDir { get { return CONFIG_DIR; } }
        internal override String ConfigFile { get { return CONFIG_FILE; } }

        [Newtonsoft.Json.JsonPropertyAttribute("manufacturerId")]
        public ulong ManufacturerId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("applicationName")]
        public string ApplicationName { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("websiteUrl")]
        public string WebsiteURL { get; set; }

        public static ManufacturerConfig Load()
        {
            if (!File.Exists(CONFIG_FILE))
            {
                Console.WriteLine("Could not find config file at " + CONFIG_FILE);
                Console.WriteLine("Please read the included README for instructions.");
            }
            using (StreamReader reader = File.OpenText(CONFIG_FILE))
            {
                return (ManufacturerConfig)JToken.ReadFrom(new JsonTextReader(reader))
                    .ToObject(typeof(ManufacturerConfig));
            }
        }

    }
}

