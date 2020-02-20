using System;
using System.Collections.Generic;
using System.Linq;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Accounts
    /// service in the Content API for Shopping.  It also exports some
    /// auxilliary functions for the main program.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Accounts.get</description>
    /// </item></list></para>
    /// </summary>
    public class AccountsSample
    {
        private ShoppingContentService service;

        /// <summary>Initializes a new instance of the <see cref="AccountsSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        public AccountsSample(ShoppingContentService service)
        {
            this.service = service;
        }

        /// <summary>Runs multiple requests against the Content API for Shopping.</summary>
        internal void RunCalls(
            ulong merchantId, string emailAddress = null, ulong? googleAdsAccountId = null)
        {
            // Can get information about your own account if non-MCA.
            GetAccount(merchantId);

            // Primary account, user management
            if (!String.IsNullOrEmpty(emailAddress))
            {
                AccountUser user = AddUser(merchantId, merchantId, emailAddress);
                RemoveUser(merchantId, merchantId, user.EmailAddress);
            }

            // Primary account, Google Ads account link
            if (googleAdsAccountId != null && googleAdsAccountId.Value != 0L)
            {
                LinkGoogleAdsAccount(merchantId, googleAdsAccountId.Value);
                UnlinkGoogleAdsAccount(merchantId, googleAdsAccountId.Value);
            }

        }

        /// <summary>
        /// Retrieves information about the current Merchant Center account.
        /// </summary>
        /// <returns>The account information for the merchantId.</returns>
        private Account GetAccount(ulong merchantId)
        {
            return service.Accounts.Get(merchantId, merchantId).Execute();
        }

        /// <summary>
        /// Adds a user to the primary account.
        /// </summary>
        /// <returns>The user that was created.</returns>
        private AccountUser AddUser(ulong merchantId, ulong accountId, String emailAddress)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Linking account {0}", emailAddress));
            Console.WriteLine("=================================================================");

            // First, retrieve list of users.
            Account account = service.Accounts.Get(merchantId, accountId).Execute();
            AccountUser newAccountUser = new AccountUser {
              EmailAddress = emailAddress,
              Admin = false
            };

            if (account.Users == null)
            {
                account.Users = new List<AccountUser>();
            }

            account.Users.Add(newAccountUser);

            // Set ETag to null as Update() will reject it otherwise.
            account.ETag = null;

            // Update the new list of accounts.
            Account response = service.Accounts.Update(account, merchantId, accountId).Execute();

            Console.WriteLine("User \"{0}\" was added to account {1}.", emailAddress, response.Id);
            Console.WriteLine();

            return newAccountUser;
        }

        /// <summary>
        /// Removes a user from the primary account.
        /// </summary>
        private void RemoveUser(ulong merchantId, ulong accountId, String emailAddress)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Unlinking account {0}", emailAddress));
            Console.WriteLine("=================================================================");

            // First, retrieve list of users.
            Account body = service.Accounts.Get(merchantId, accountId).Execute();

            body.Users.Remove(body.Users.Where(x => x.EmailAddress == emailAddress).First());

            // Set ETag to null as Update() will reject it otherwise.
            body.ETag = null;

            // Update the new list of accounts.
            Account response = service.Accounts.Update(body, merchantId, accountId).Execute();

            Console.WriteLine("User \"{0}\" was deleted from account {1}.", emailAddress, response.Id);
            Console.WriteLine();
        }

        /// <summary>
        /// Links the specified Google Ads account to the specified merchant center account.
        /// </summary>
        /// <returns>The account that was linked.</returns>
        private AccountAdsLink LinkGoogleAdsAccount(ulong merchantId, ulong googleAdsAccountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Linking Google Ads account {0}", googleAdsAccountId));
            Console.WriteLine("=================================================================");

            // First, retrieve list of Google Ads accounts.
            Account account = service.Accounts.Get(merchantId, merchantId).Execute();
            var newAccountLink = new AccountAdsLink {
              AdsId = googleAdsAccountId,
              Status = "active"
            };

            if (account.AdsLinks == null)
            {
                account.AdsLinks = new List<AccountAdsLink>();
            }
            account.AdsLinks.Add(newAccountLink);

            // Set ETag to null as Update() will reject it otherwise.
            account.ETag = null;

            // Update the new list of links.
            Account response = service.Accounts.Update(account, merchantId, merchantId).Execute();

            Console.WriteLine("GoogleAds account \"{0}\" was added to account {1}.", googleAdsAccountId, response.Id);
            Console.WriteLine();

            return newAccountLink;
        }

        /// <summary>
        /// Unlinks the specified Google Ads account to the specified merchant center.
        /// </summary>
        private void UnlinkGoogleAdsAccount(ulong merchantId, ulong googleAdsAccountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Unlinking Google Ads account {0}", googleAdsAccountId));
            Console.WriteLine("=================================================================");

            // First, retrieve list of Google Ads accounts.
            Account body = service.Accounts.Get(merchantId, merchantId).Execute();

            body.AdsLinks.Remove(body.AdsLinks.Where(x => x.AdsId == googleAdsAccountId).First());

            // Set ETag to null as Update() will reject it otherwise.
            body.ETag = null;

            // Update the new list of links.
            Account response = service.Accounts.Update(body, merchantId, merchantId).Execute();

            Console.WriteLine("Google Ads account \"{0}\" was unlinked from account {1}.", googleAdsAccountId, response.Id);
            Console.WriteLine();
        }
    }
}
