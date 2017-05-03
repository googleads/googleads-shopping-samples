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
            // Retrieve whether the configured MC account is an MCA via the API.
            config.IsMCA = retrieveMCAStatus(service, config);

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
