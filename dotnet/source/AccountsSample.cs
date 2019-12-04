using System;
using System.Collections.Generic;
using System.Linq;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;

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
            ulong merchantId, string emailAddress = null, ulong? adWordsAccountId = null)
        {
            // Can get information about your own account if non-MCA.
            GetAccount(merchantId);

            // Primary account, user management
            if (!String.IsNullOrEmpty(emailAddress))
            {
                AccountUser user = AddUser(merchantId, merchantId, emailAddress);
                RemoveUser(merchantId, merchantId, user.EmailAddress);
            }

            // Primary account, AdWords account link
            if (adWordsAccountId != null && adWordsAccountId.Value != 0L)
            {
                LinkAdWordsAccount(merchantId, adWordsAccountId.Value);
                UnlinkAdWordsAccount(merchantId, adWordsAccountId.Value);
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
            AccountUser newAccountUser = new AccountUser();
            newAccountUser.EmailAddress = emailAddress;
            newAccountUser.Admin = false;

            account.Users.Add(newAccountUser);

            // Set ETag to null as Patch() will reject it otherwise.
            account.ETag = null;

            // Patch the new list of accounts.
            Account response = service.Accounts.Patch(account, merchantId, accountId).Execute();

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

            // Set ETag to null as Patch() will reject it otherwise.
            body.ETag = null;

            // Patch the new list of accounts.
            Account response = service.Accounts.Patch(body, merchantId, accountId).Execute();

            Console.WriteLine("User \"{0}\" was deleted from account {1}.", emailAddress, response.Id);
            Console.WriteLine();
        }

        /// <summary>
        /// Links the specified AdWords account to the specified merchant center account.
        /// </summary>
        /// <returns>The account that was linked.</returns>
        private AccountAdwordsLink LinkAdWordsAccount(ulong merchantId, ulong adWordsAccountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Linking AdWords account {0}", adWordsAccountId));
            Console.WriteLine("=================================================================");

            // First, retrieve list of AdWords accounts.
            Account account = service.Accounts.Get(merchantId, merchantId).Execute();
            var newAccountAdWords = new AccountAdwordsLink();
            newAccountAdWords.AdwordsId = adWordsAccountId;
            newAccountAdWords.Status = "active";

            if (account.AdwordsLinks == null)
            {
                account.AdwordsLinks = new List<AccountAdwordsLink>();
            }
            account.AdwordsLinks.Add(newAccountAdWords);

            // Set ETag to null as Patch() will reject it otherwise.
            account.ETag = null;

            // Patch the new list of links.
            Account response = service.Accounts.Patch(account, merchantId, merchantId).Execute();

            Console.WriteLine("AdWords account \"{0}\" was added to account {1}.", adWordsAccountId, response.Id);
            Console.WriteLine();

            return newAccountAdWords;
        }

        /// <summary>
        /// Unlinks the specified AdWords account to the specified merchant center.
        /// </summary>
        private void UnlinkAdWordsAccount(ulong merchantId, ulong adWordsAccountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Unlinking AdWords account {0}", adWordsAccountId));
            Console.WriteLine("=================================================================");

            // First, retrieve list of AdWords accounts.
            Account body = service.Accounts.Get(merchantId, merchantId).Execute();

            body.AdwordsLinks.Remove(body.AdwordsLinks.Where(x => x.AdwordsId == adWordsAccountId).First());

            // Set ETag to null as Patch() will reject it otherwise.
            body.ETag = null;

            // Patch the new list of links.
            Account response = service.Accounts.Patch(body, merchantId, merchantId).Execute();

            Console.WriteLine("AdWords account \"{0}\" was unlinked from account {1}.", adWordsAccountId, response.Id);
            Console.WriteLine();
        }
    }
}
