using System;
using System.Collections.Generic;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the
    /// Productstatuses service in the Content API for Shopping. 
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Productstatuses.get</description>
    /// </item>
    /// <item>
    /// <description>Productstatuses.list</description>
    /// </item></list></para>
    /// </summary>
    public class ProductstatusesSample
    {
        private ShoppingContentService service;
        private int maxListPageSize;

        public ProductstatusesSample(ShoppingContentService service, int maxListPageSize)
        {
            this.service = service;
            this.maxListPageSize = maxListPageSize;
        }

        /// <summary>Runs multiple requests against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId)
        {
            GetAllProducts(merchantId);
        }

        /// <summary>
        /// Retrieves the status a particular product.
        /// </summary>
        /// <returns>The status information for the offerId.</returns>
        private ProductStatus GetProduct(ulong merchantId, string offerId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Product Status for {0}", offerId);
            Console.WriteLine("=================================================================");

            ProductStatus status = service.Productstatuses.Get(merchantId, offerId).Execute();

            return status;
        }

        /// <summary>
        /// Retrieves the statuses of all products for the account.
        /// </summary>
        /// <returns>The last page of products.</returns>
        private ProductstatusesListResponse GetAllProducts(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Product Statuses");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            string pageToken = null;
            ProductstatusesListResponse statusesResponse = null;

            do
            {
                ProductstatusesResource.ListRequest statusesRequest =
                    service.Productstatuses.List(merchantId);
                statusesRequest.MaxResults = maxListPageSize;
                statusesRequest.PageToken = pageToken;

                statusesResponse = statusesRequest.Execute();

                if (statusesResponse.Resources != null && statusesResponse.Resources.Count != 0)
                {
                    foreach (var status in statusesResponse.Resources)
                    {
                        Console.WriteLine(
                            "Product with ID \"{0}\" and title \"{1}\" was found.",
                            status.ProductId,
                            status.Title);
                        if (status.DataQualityIssues == null)
                        {
                            Console.WriteLine("- No data quality issues.");
                        }
                        else {
                            PrintDataQualityIssues(status.DataQualityIssues);
                        }
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

        private void PrintDataQualityIssues(IList<ProductStatusDataQualityIssue> issues)
        {
            Console.WriteLine("{0} data quality issues found:", issues.Count);
            foreach (var issue in issues)
            {
                if (issue.Detail != null)
                {
                    Console.WriteLine(
                        "- ({0}) [{1}]: {2}", issue.Severity, issue.Id, issue.Detail);
                }
                else
                {
                    Console.WriteLine(
                        "- ({0}) [{1}]", issue.Severity, issue.Id);
                }
            }
        }
    }
}
