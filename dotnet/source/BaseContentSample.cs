using System;
using System.Linq;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.Services;
using System.IO;

namespace ShoppingSamples.Content
{
    internal abstract class BaseContentSample : BaseSample
    {
        // BasePath/BaseUri do not expose setters, so we subclass ShoppingContentService
        // so we can override their values.
        private class ShoppingContentServiceWithBaseUri : ShoppingContentService
        {
            private string baseUri;
            private string basePath;

            public override string BaseUri { get { return this.baseUri; } }
            public override string BasePath { get { return this.basePath; } }

            public ShoppingContentServiceWithBaseUri(
                BaseClientService.Initializer init, Uri u)
                : base(init)
            {
                this.baseUri = u.AbsoluteUri;
                this.basePath = u.AbsolutePath;
            }
        }

        protected MerchantConfig config;
        protected ShoppingContentService service;
        protected ShoppingContentService sandboxService;

        internal override BaseConfig Config { get { return config; } }
        internal override string Scope { get { return ShoppingContentService.Scope.Content; } }
        internal override string ApiName { get { return "Content API for Shopping"; } }
        internal override IClientService Service { get { return service; } }

        internal override void initializeConfig(bool noConfig)
        {
            if (noConfig)
            {
                config = new MerchantConfig();
            }
            else
            {
                config = MerchantConfig.Load(CliOptions.ConfigPath);
            }
        }

        internal override void initializeService(BaseClientService.Initializer init)
        {
            service = new ShoppingContentService(init);
            retrieveConfiguration(service, config);
            createSandbox(init, new Uri(service.BaseUri));
        }

        internal override void initializeService(BaseClientService.Initializer init, Uri u)
        {
            service = new ShoppingContentServiceWithBaseUri(init, u);
            retrieveConfiguration(service, config);
            createSandbox(init, u);
        }

        private void createSandbox(BaseClientService.Initializer init, Uri u) {
            var pathParts = service.BasePath
                .Split(new char[] {'/'}, StringSplitOptions.RemoveEmptyEntries);
            var basename = pathParts.Last();
            if (basename == "v2")
            {
                var newPath = "/"
                    + String.Join("/", pathParts.Take(pathParts.Count() - 1))
                    + "/v2sandbox/";
                sandboxService = new ShoppingContentServiceWithBaseUri(init, new Uri(u, newPath));
            }
            else
            {
                Console.WriteLine("Using same API endpoint for sandbox methods.");
                sandboxService = service;
            }
        }

        // Retrieve the following configuration fields using the Content API:
        // - IsMCA
        // - WebsiteUrl
        // Also use the first Merchant Center account to which the authenticated user has access
        // if no Merchant Center ID was provided.
        internal void retrieveConfiguration(ShoppingContentService service, MerchantConfig config)
        {
            Console.WriteLine("Retrieving information for authenticated user.");
            var authinfo = service.Accounts.Authinfo().Execute();

            if (authinfo.AccountIdentifiers.Count == 0)
            {
                throw new ArgumentException(
                    "Authenticated user has no access to any Merchant Center accounts.");
            }
            if (config.MerchantId == null)
            {
                var firstAccount = authinfo.AccountIdentifiers[0];
                if (firstAccount.MerchantId == null)
                {
                    config.MerchantId = firstAccount.AggregatorId;
                }
                else
                {
                    config.MerchantId = firstAccount.MerchantId;
                }
                Console.WriteLine(
                    "Using Merchant Center {0} for running samples.", config.MerchantId.Value);
            }
            ulong merchantId = config.MerchantId.Value;

            // We detect whether the requested Merchant Center ID is an MCA by checking
            // Accounts.authinfo(). If it is an MCA, then the authenticated user must be
            // a user of that account, which means it'll be listed here, and it must
            // appear in the AggregatorId field of one of the AccountIdentifier entries.
            config.IsMCA = false;
            foreach (var accountId in authinfo.AccountIdentifiers) {
                if (merchantId == accountId.AggregatorId)
                {
                    config.IsMCA = true;
                    break;
                }
                if (merchantId == accountId.MerchantId)
                {
                    break;
                }
            }
            Console.WriteLine("Merchant Center {0} is{1} an MCA.",
                merchantId,
                config.IsMCA ? "" : " not");

            var account = service.Accounts.Get(merchantId, merchantId).Execute();
            if (!String.IsNullOrEmpty(account.WebsiteUrl))
            {
                config.WebsiteURL = account.WebsiteUrl;
                Console.WriteLine(
                    "Website for Merchant Center {0}: {1}",
                    merchantId,
                    config.WebsiteURL);
            }
            else
            {
                Console.WriteLine("Merchant Center {0} has no configured website.", merchantId);
            }
        }
    }
}
