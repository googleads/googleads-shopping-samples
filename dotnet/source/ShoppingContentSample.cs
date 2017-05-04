using System;
using System.Threading;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Services;
using Google.Apis.ShoppingContent.v2;
using CommandLine;

namespace ShoppingSamples.Content
{
    internal class ShoppingContentSample : BaseContentSample
    {
        private static readonly int MaxListPageSize = 50;


        internal override void runCalls()
        {
            AccountsSample accountsSample = new AccountsSample(service);
            AccountstatusesSample accountstatusesSample =
                new AccountstatusesSample(service, MaxListPageSize);
            AccounttaxSample accounttaxSample = new AccounttaxSample(service);
            DatafeedsSample datafeedsSample = new DatafeedsSample(service);
            ProductsSample productsSample = new ProductsSample(service, MaxListPageSize);
            ProductstatusesSample productstatusesSample =
                new ProductstatusesSample(service, MaxListPageSize);
            ShippingsettingsSample shippingsettingsSample = new ShippingsettingsSample(service);
            MultiClientAccountSample multiClientAccountSample =
                new MultiClientAccountSample(service);

            if (!config.IsMCA)
            {
                // Non-MCA calls
                productsSample.RunCalls(config.MerchantId, config.WebsiteURL);
                productstatusesSample.RunCalls(config.MerchantId);
                datafeedsSample.RunCalls(config.MerchantId);
                accountstatusesSample.RunCalls(config.MerchantId);
                accountsSample.RunCalls(config.MerchantId, config.AccountSampleUser,
                    config.AccountSampleAdWordsCID);
                accounttaxSample.RunCalls(config.MerchantId);
                shippingsettingsSample.RunCalls(config.MerchantId);
            }
            else
            {
                // MCA calls
                accountstatusesSample.RunMultiCalls(config.MerchantId);
                multiClientAccountSample.RunCalls(config.MerchantId);
            }
        }
    }
}
