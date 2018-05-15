using System;
using System.Collections.Generic;
using System.Linq;
using Google.Apis.ManufacturerCenter.v1;
using Google.Apis.ManufacturerCenter.v1.Data;

namespace ShoppingSamples.Manufacturers
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Products
    /// service in the Manufacturer Center API.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <item>
    /// <description>Products.get</description>
    /// </item>
    /// <item>
    /// <description>Products.list</description>
    /// </item></para>
    /// </summary>
    public class ProductsSample
    {
        private ManufacturerCenterService service;
        private int maxListPageSize;

        /// <summary>Initializes a new instance of the <see cref="ProductsSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        /// <param name="maxListPageSize">The maximum page size to retrieve.</param>
        public ProductsSample(ManufacturerCenterService service, int maxListPageSize)
        {
            this.service = service;
            this.maxListPageSize = maxListPageSize;
        }

        /// <summary>Runs requests against the Products service.</summary>
        internal void RunCalls(string manufacturerId)
        {
            GetAllProducts(manufacturerId);
        }

        /// <summary>Gets and prints all products for the given manufacturer ID.</summary>
        /// <returns>The last page of retrieved products.</returns>
        private ListProductsResponse GetAllProducts(string manufacturerId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Products");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            string pageToken = null;
            ListProductsResponse productsResponse = null;

            AccountsResource.ProductsResource.ListRequest listRequest =
                service.Accounts.Products.List(manufacturerId);
            listRequest.PageSize = maxListPageSize;
            do
            {
                listRequest.PageToken = pageToken;

                productsResponse = listRequest.Execute();

                if (productsResponse.Products != null && productsResponse.Products.Count != 0)
                {
                    foreach (var product in productsResponse.Products)
                    {
                        Console.WriteLine(
                            "Product with ID \"{0}\" and title \"{1}\" was found.",
                            product.ProductId,
                            product.Attributes.Title);
                        PrintIssues(product.Issues);
                    }
                }
                else {
                    Console.WriteLine("No accounts found.");
                }

                pageToken = productsResponse.NextPageToken;
            } while (pageToken != null);
            Console.WriteLine();

            // Return the last page of accounts.
            return productsResponse;
        }

        /// <summary>
        /// Prints out a list of issues from the Manufacturer Center API.
        /// </summary>
        internal void PrintIssues(IList<Issue> issues)
        {
            if (issues == null) return;
            Console.WriteLine("Received the following issues:");
            foreach (Issue issue in issues)
            {
                Console.Write("({0}, {1})", issue.Severity, issue.Resolution);
                if (issue.Attribute != null)
                {
                    Console.Write("[{0}] ", issue.Attribute);
                }
                Console.WriteLine(issue.Type + ": " + issue.Title);
                Console.WriteLine("  " + issue.Description);
            }
        }
    }
}
