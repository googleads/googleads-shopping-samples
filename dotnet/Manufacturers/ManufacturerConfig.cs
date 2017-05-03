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
        public override String ConfigDir { get; set; }
        internal override String ConfigFile { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("manufacturerId")]
        public ulong ManufacturerId { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("websiteUrl")]
        public string WebsiteURL { get; set; }

        public static ManufacturerConfig Load(string configPath)
        {
            ManufacturerConfig config;

            var manufacturersPath = Path.Combine(configPath, "manufacturers");
            if (!Directory.Exists(manufacturersPath))
            {
                Console.WriteLine("Could not find configuration directory at " + manufacturersPath);
                Console.WriteLine("Please read the included README for instructions.");
                throw new FileNotFoundException("Missing configuration directory");
            }

            var manufacturersFile = Path.Combine(manufacturersPath, "manufacturer-info.json");
            if (!File.Exists(manufacturersFile))
            {
                Console.WriteLine("Could not find configuration file at " + manufacturersFile);
                Console.WriteLine("Please read the included README for instructions.");
                throw new FileNotFoundException("Missing configuration file");
            }

            using (StreamReader reader = File.OpenText(manufacturersFile))
            {
                config = (ManufacturerConfig)JToken.ReadFrom(new JsonTextReader(reader))
                    .ToObject(typeof(ManufacturerConfig));
                config.ConfigDir = manufacturersPath;
                config.ConfigFile = manufacturersFile;
            }

            return config;
        }
    }
}

