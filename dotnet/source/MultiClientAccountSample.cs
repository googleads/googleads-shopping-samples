using System;
using System.Collections.Generic;
using System.Net;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Content API for Shopping
    /// that are useful for Multi-Client Accounts (MCAs).
    /// </summary>
    public class MultiClientAccountSample
    {
        private ShoppingContentService service;
        ShoppingUtil shoppingUtil = new ShoppingUtil();
        // Currently, we may receive a 401 Unauthorized error if the (sub-)account is not yet
        // available soon after creating it, so retry if we see one while making a modification
        // to or deleting an account. The specific HTTP error we receive may be subject to change.
        IEnumerable<HttpStatusCode> retryCodes = new HttpStatusCode[] {HttpStatusCode.Unauthorized};


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
            UpdateAccount(merchantId, (ulong)newAccount.Id);
            DeleteAccount(merchantId, (ulong)newAccount.Id);
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

            var accountGetRequest = service.Accounts.Get(merchantId, accountId);
            Account account = shoppingUtil.ExecuteWithRetries(accountGetRequest, retryCodes);
            account.Name = $"updated-account{shoppingUtil.GetUniqueId()}";

            // Set ETag to null as Update() will reject it otherwise.
            account.ETag = null;

            var request = service.Accounts.Update(account, merchantId, accountId);
            Account response = shoppingUtil.ExecuteWithRetries(request, retryCodes);
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

            var request = service.Accounts.Delete(merchantId, accountId);
            shoppingUtil.ExecuteWithRetries(request, retryCodes);

            Console.WriteLine("Account with ID \"{0}\" was deleted.", accountId);
            Console.WriteLine();
        }

        internal Account GenerateAccount()
        {
            String name = String.Format("account{0}", shoppingUtil.GetUniqueId());
            Account account = new Account {
              Name = name,
              WebsiteUrl = String.Format("https://{0}.example.com/", name)
            };
            return account;
        }
    }
}
