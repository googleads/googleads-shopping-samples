using System;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.Services;

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
        }

        internal override void initializeService(BaseClientService.Initializer init, Uri u)
        {
            service = new ShoppingContentServiceWithBaseUri(init, u);
        }
    }
}

