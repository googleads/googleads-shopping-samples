using System;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;

namespace ContentShoppingSamples
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Content API for Shopping
    /// that are useful for Multi-Client Accounts (MCAs).
    /// </summary>
    public class MultiClientAccountSample
    {
        private ShoppingContentService service;
        ShoppingUtil shoppingUtil = new ShoppingUtil();

        /// <summary>Initializes a new instance of the <see cref="ShoppingcontentApiConsumer"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        public MultiClientAccountSample(ShoppingContentService service)
        {
            this.service = service;
        }

        internal void RunCalls(ulong merchantId)
        {
            GetAllAccounts(merchantId);
            Account newAccount = InsertAccount(merchantId);
            try
            {
                UpdateAccount(merchantId, (ulong)newAccount.Id);
            }
            catch (Google.GoogleApiException e)
            {
                Console.WriteLine("Warning: Tried to update an account too soon after creation. " + e.Message);
            }
            try
            {
                DeleteAccount(merchantId, (ulong)newAccount.Id);
            }
            catch (Google.GoogleApiException e)
            {
                Console.WriteLine("Warning: Tried to delete an account too soon after creation. " + e.Message);
            }
        }

        /// <summary>Gets all accounts on the specified multi-client account</summary>
        /// <returns>The last page of retrieved accounts.</returns>
        private AccountsListResponse GetAllAccounts(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Accounts");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            AccountsListResponse accountsResponse = null;

            AccountsResource.ListRequest accountRequest = service.Accounts.List(merchantId);
            accountsResponse = accountRequest.Execute();

            if (accountsResponse.Resources != null && accountsResponse.Resources.Count != 0)
            {
                foreach (var account in accountsResponse.Resources)
                {
                    Console.WriteLine(
                        "Account with ID \"{0}\" and name \"{1}\" was found.",
                        account.Id,
                        account.Name);
                }
            }
            else {
                Console.WriteLine("No accounts found.");
            }

            Console.WriteLine();

            // Return the last page of accounts.
            return accountsResponse;
        }

        /// <summary>
        /// Updates the specified account on the specified multi-client account.
        /// </summary>
        private void UpdateAccount(ulong merchantId, ulong accountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Updating account {0}", accountId));
            Console.WriteLine("=================================================================");
            // First we need to retrieve the full object, since there are no partial updates for the
            // accounts collection in Content API v2.

            Account account = service.Accounts.Get(merchantId, accountId).Execute();

            // Set ETag to null as Patch() will reject it otherwise.
            account.ETag = null;
            account.Name = "updated-account" + shoppingUtil.GetUniqueId();

            Account response = service.Accounts.Patch(account, merchantId, accountId).Execute();
            Console.WriteLine(
                "Account updated with ID \"{0}\" and name \"{1}\".",
                response.Id,
                response.Name);
            Console.WriteLine();
        }

        /// <summary>
        /// This example adds an account to a specified multi-client account.
        /// </summary>
        /// <returns>The account that was inserter</returns>
        private Account InsertAccount(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Inserting a account");
            Console.WriteLine("=================================================================");
            Account account = GenerateAccount();

            Account response = service.Accounts.Insert(account, merchantId).Execute();
            Console.WriteLine(
                "Account inserted with ID \"{0}\" and name \"{1}\".",
                response.Id,
                response.Name);
            Console.WriteLine();
            return response;
        }

        /// <summary>
        /// Removes an account from the specified multi-client account.
        /// </summary>
        private void DeleteAccount(ulong merchantId, ulong accountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Deleting account {0}", accountId));
            Console.WriteLine("=================================================================");

            service.Accounts.Delete(merchantId, accountId).Execute();

            Console.WriteLine("Account with ID \"{0}\" was deleted.", accountId);
            Console.WriteLine();
        }

        internal Account GenerateAccount()
        {
            String name = String.Format("account{0}", shoppingUtil.GetUniqueId());
            Account account = new Account();
            account.Name = name;
            account.WebsiteUrl = String.Format("https://{0}.example.com/", name);
            return account;
        }
    }
}
