using System;
using System.Collections.Generic;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;

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
                        PrintStatus(status);
                    }
                }
                else
                {
                    Console.WriteLine("No products found.");
                }

                pageToken = statusesResponse.NextPageToken;
            } while (pageToken != null);
            Console.WriteLine();

            // Return the last page of accounts.
            return statusesResponse;
        }

        private void PrintStatus(ProductStatus status)
        {
            Console.WriteLine("Information for product {0}:", status.ProductId);
            Console.WriteLine("- Title: {0}", status.Title);

            Console.WriteLine("- Destination statuses:");
            foreach (var stat in status.DestinationStatuses)
            {
                Console.WriteLine("  - {0}: {1}", stat.Destination, stat.Status);
            }

            if (status.ItemLevelIssues == null)
            {
                Console.WriteLine("- No issues.");
            }
            else
            {
                var issues = status.ItemLevelIssues;
                Console.WriteLine("- There are {0} issues:", issues.Count);
                foreach (var issue in issues)
                {
                    Console.WriteLine("  - Code: {0}", issue.Code);
                    Console.WriteLine("    Description: {0}", issue.Description);
                    Console.WriteLine("    Detailed description: {0}", issue.Detail);
                    Console.WriteLine("    Resolution: {0}", issue.Resolution);
                    Console.WriteLine("    Servability: {0}", issue.Servability);
                }
            }
        }
    }
}
