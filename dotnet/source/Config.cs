using System;
using System.IO;
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

        private ulong _merchantId;
        public ulong MerchantId { get { return _merchantId; } }

        private string _applicationName;
        public string ApplicationName { get { return _applicationName; } }

        private string _emailAddress;
        public string EmailAddress { get { return _emailAddress; } }

        private string _accountSampleUser;
        public string AccountSampleUser { get { return _accountSampleUser; } }

        private ulong _accountSampleAdWordsCID;
        public ulong AccountSampleAdWordsCID { get { return _accountSampleAdWordsCID; } }

        private bool _isMCA;
        public bool IsMCA { get { return _isMCA; } }

        private Config(ulong merchantId, string applicationName, string emailAddress,
                        string accountSampleUser, ulong accountSampleAdWordsCID, bool isMCA)
        {
            _merchantId = merchantId;
            _applicationName = applicationName;
            _emailAddress = emailAddress;
            _accountSampleUser = accountSampleUser;
            _accountSampleAdWordsCID = accountSampleAdWordsCID;
            _isMCA = isMCA;
        }

        public static Config Load()
        {
            if (!File.Exists(CONFIG_FILE))
            {
                Console.WriteLine("Could not find config file at " + Config.CONFIG_FILE);
                Console.WriteLine("Please read the included README for instructions.");
            }
            using (StreamReader reader = File.OpenText(CONFIG_FILE))
            {
                JObject o = (JObject)JToken.ReadFrom(new JsonTextReader(reader));
                string merchantIdString = (string)o.SelectToken("merchantId");
                ulong merchantId = 0;
                if (merchantIdString != null)
                {
                    merchantId = Convert.ToUInt64(merchantIdString);
                }
                string applicationName = (string)o.SelectToken("applicationName");
                string emailAddress = (string)o.SelectToken("emailAddress");
                string accountSampleUser = (string)o.SelectToken("accountSampleUser");
                string accountSampleAdWordsCIDString = (string)o.SelectToken("accountSampleAdWordsCID");
                ulong accountSampleAdWordsCID = 0;
                if (accountSampleAdWordsCIDString != null)
                {
                    accountSampleAdWordsCID = Convert.ToUInt64(accountSampleAdWordsCIDString);
                }
                bool isMCA = (bool)o.SelectToken("isMCA");
                return new Config(merchantId, applicationName, emailAddress,
                                   accountSampleUser, accountSampleAdWordsCID, isMCA);
            }
        }
    }
}
