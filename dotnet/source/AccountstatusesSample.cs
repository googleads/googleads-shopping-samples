using System;
using System.Collections.Generic;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the
    /// Accountstatuses service in the Content API for Shopping. 
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Accountstatuses.get</description>
    /// </item>
    /// <item>
    /// <description>Accountstatuses.list</description>
    /// </item></list></para>
    /// </summary>
    public class AccountstatusesSample
    {
        private ShoppingContentService service;
        private int maxListPageSize;

        public AccountstatusesSample(ShoppingContentService service, int maxPageListSize)
        {
            this.service = service;
            this.maxListPageSize = maxPageListSize;
        }

        /// <summary>Runs requests for non-multi-client Merchant Center accounts
        /// against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId)
        {
            GetAccount(merchantId, merchantId);
        }

        /// <summary>Runs requests for multi-client Merchant Center accounts
        /// against the Content API for Shopping.</summary>
        internal void RunMultiCalls(ulong merchantId)
        {
            GetAllAccounts(merchantId);
        }

        /// <summary>
        /// Retrieves the status of a particular account.
        /// </summary>
        /// <returns>The status information for the specified account.</returns>
        private AccountStatus GetAccount(ulong merchantId, ulong accountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Product Status for {0}", accountId);
            Console.WriteLine("=================================================================");

            AccountStatus status = service.Accountstatuses.Get(merchantId, accountId).Execute();
            PrintAccountStatus(status);
            Console.WriteLine();

            return status;
        }

        /// <summary>
        /// Retrieves the statuses of all subaccounts.
        /// </summary>
        /// <returns>The last page of account statuses.</returns>
        private AccountstatusesListResponse GetAllAccounts(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Account Statuses");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            string pageToken = null;
            AccountstatusesListResponse statusesResponse = null;

            do
            {
                AccountstatusesResource.ListRequest statusesRequest =
                    service.Accountstatuses.List(merchantId);
                statusesRequest.MaxResults = maxListPageSize;
                statusesRequest.PageToken = pageToken;

                statusesResponse = statusesRequest.Execute();

                if (statusesResponse.Resources != null && statusesResponse.Resources.Count != 0)
                {
                    foreach (var status in statusesResponse.Resources)
                    {
                        PrintAccountStatus(status);
                    }
                }
                else
                {
                    Console.WriteLine("No accounts found.");
                }

                pageToken = statusesResponse.NextPageToken;
            } while (pageToken != null);
            Console.WriteLine();

            // Return the last page of accounts.
            return statusesResponse;
        }

        private void PrintAccountStatus(AccountStatus status)
        {
            Console.WriteLine("Account {0} found.", status.AccountId);
            IList<AccountStatusDataQualityIssue> issues = status.DataQualityIssues;

            if (issues == null)
            {
                Console.WriteLine("- No data quality issues.");
            }
            else
            {
                Console.WriteLine("{0} data quality issues found:", issues.Count);

                foreach (var issue in issues)
                {
                    Console.WriteLine("- ({0}) [{1}]", issue.Severity, issue.Id);
                    Console.WriteLine("  Affects {0} items, {1} examples follow:", issue.NumItems, issue.ExampleItems.Count);
                    foreach (var example in issue.ExampleItems)
                    {
                        Console.WriteLine("  - {0}: {1}", example.ItemId, example.Title);
                    }
                }
            }
        }
    }
}
