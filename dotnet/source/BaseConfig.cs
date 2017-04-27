using System;
using System.IO;
using Google.Apis.Auth.OAuth2.Responses;
using Newtonsoft.Json;

namespace ShoppingSamples
{
    /// <summary>
    /// A data class for storing the info needed to authenticate API calls.
    /// </summary>
    abstract class BaseConfig
    {
        public abstract String ConfigDir { get; set; }
        internal abstract String ConfigFile { get; set; }
    
        [Newtonsoft.Json.JsonPropertyAttribute("emailAddress")]
        public string EmailAddress { get; set; }

        [Newtonsoft.Json.JsonPropertyAttribute("token")]
        public TokenResponse Token { get; set; }

        public void Save()
        {
            JsonSerializer serializer = new JsonSerializer();
            using (StreamWriter sw = new StreamWriter(ConfigFile))
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

