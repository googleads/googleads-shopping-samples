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

            ulong merchantId = config.MerchantId.Value;

            if (!config.IsMCA)
            {
                // Non-MCA calls
                productsSample.RunCalls(merchantId, config.WebsiteURL);
                productstatusesSample.RunCalls(merchantId);
                datafeedsSample.RunCalls(merchantId);
                accountstatusesSample.RunCalls(merchantId);
                accountsSample.RunCalls(
                    merchantId, config.AccountSampleUser, config.AccountSampleAdWordsCID);
                accounttaxSample.RunCalls(merchantId);
                shippingsettingsSample.RunCalls(merchantId);
            }
            else
            {
                // MCA calls
                accountstatusesSample.RunMultiCalls(merchantId);
                multiClientAccountSample.RunCalls(merchantId);
            }
        }
    }
}
