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

        internal override void initializeConfig()
        {
            config = MerchantConfig.Load(CliOptions.ConfigPath);
        }

        internal override void initializeService(BaseClientService.Initializer init)
        {
            service = new ShoppingContentService(init);
            // Retrieve whether the configured MC account is an MCA via the API.
            // The sandbox service only has access to Orders methods, so can't
            // use it for this.
            config.IsMCA = retrieveMCAStatus(service, config);
            createSandbox(init, new Uri(service.BaseUri));
        }

        internal override void initializeService(BaseClientService.Initializer init, Uri u)
        {
            service = new ShoppingContentServiceWithBaseUri(init, u);
            config.IsMCA = retrieveMCAStatus(service, config);
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

        internal bool retrieveMCAStatus(ShoppingContentService service, MerchantConfig config)
        {
            Console.WriteLine("Retrieving MCA status for configured account.");
            // The resource returned by Accounts.get() does not have the MCA status, but if
            // the authenticated user is directly listed as a user of the Merchant Center account
            // in question, we can see whether it is an MCA or not by calling Accounts.authinfo().
            var authinfo = service.Accounts.Authinfo().Execute();
            foreach (var accountId in authinfo.AccountIdentifiers) {
                if (config.MerchantId == accountId.AggregatorId)
                {
                    return true;
                }
                if (config.MerchantId == accountId.MerchantId)
                {
                    return false;
                }
            }
            // If the configured account wasn't listed in the authinfo response, then either
            // it is a sub-account of an MCA that was listed, or the authenticated user does
            // not have access. Check this by trying to call Accounts.get().
            try {
                service.Accounts.Get(config.MerchantId, config.MerchantId).Execute();
            } catch (Google.GoogleApiException) {
                throw new ArgumentException(String.Format(
                    "Authenticated user does not have access to account {0}.", config.MerchantId));
            }
            // Sub-accounts cannot be MCAs.
            return false;
        }
    }
}
