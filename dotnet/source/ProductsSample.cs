using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Products
    /// service in the Content API for Shopping.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Products.insert</description>
    /// </item>
    /// <item>
    /// <description>Products.custombatch</description>
    /// </item>
    /// <item>
    /// <description>Products.get</description>
    /// </item>
    /// <item>
    /// <description>Products.list</description>
    /// </item>
    /// <item>
    /// <description>Products.delete</description>
    /// </item>
    /// </list></para>
    /// </summary>
    public class ProductsSample
    {
        private ShoppingContentService service;
        private int maxListPageSize;
        ShoppingUtil shoppingUtil = new ShoppingUtil();
        IEnumerable<HttpStatusCode> retryCodes = new HttpStatusCode[] {HttpStatusCode.NotFound};

        /// <summary>Initializes a new instance of the <see cref="ProductsSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        /// <param name="maxListPageSize">The maximum page size to retrieve.</param>
        public ProductsSample(ShoppingContentService service, int maxListPageSize)
        {
            this.service = service;
            this.maxListPageSize = maxListPageSize;
        }

        /// <summary>Runs multiple requests against the Products service.</summary>
        internal void RunCalls(ulong merchantId, string websiteUrl = null)
        {
            if (websiteUrl == null)
            {
                Console.WriteLine("Cannot run Products workflow without a configured website URL.");
                return;
            }
            // Product insertion
            Product newProduct = InsertProduct(merchantId, websiteUrl);
            List<String> productList = InsertProductCustombatch(merchantId, websiteUrl);

            GetAllProducts(merchantId);

            UpdateProduct(merchantId, newProduct.Id);

            // To show the inserted (single) product has changed.
            GetAllProducts(merchantId);

            DeleteProduct(merchantId, newProduct);
            DeleteProductCustombatch(merchantId, productList);
        }

        /// <summary>Gets and prints all products for the given merchant ID.</summary>
        /// <returns>The last page of retrieved accounts.</returns>
        private ProductsListResponse GetAllProducts(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Products");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            string pageToken = null;
            ProductsListResponse productsResponse = null;

            do
            {
                ProductsResource.ListRequest accountRequest = service.Products.List(merchantId);
                accountRequest.MaxResults = maxListPageSize;
                accountRequest.PageToken = pageToken;

                productsResponse = accountRequest.Execute();

                if (productsResponse.Resources != null && productsResponse.Resources.Count != 0)
                {
                    foreach (var product in productsResponse.Resources)
                    {
                        Console.WriteLine(
                            "Product with ID \"{0}\" and title \"{1}\" was found.",
                            product.Id,
                            product.Title);
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
        /// Updates a product using the products.insert method as there is no update method.
        /// <para>Inserting a product with an ID that already exists means the same as doing an update.</para>
        /// </summary>
        private void UpdateProduct(ulong merchantId, String productId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Updating product {0}", productId));
            Console.WriteLine("=================================================================");
            // First we need to retrieve the full object, since there are no partial updates for the
            // products collection in Content API v2.1.

            var request = service.Products.Get(merchantId, productId);
            Product product = shoppingUtil.ExecuteWithRetries(request, retryCodes);

            // Set ETag to null as Insert() will reject it otherwise.
            product.ETag = null;

            product.ProductTypes = new List<String>();
            product.ProductTypes.Add("English/Classics");

            // Before inserting, product.Source needs to be cleared.
            product.Source = null;

            Product response = service.Products.Insert(product, merchantId).Execute();
            Console.WriteLine(
                "Product updated with ID \"{0}\" and title \"{1}\".",
                response.Id,
                response.Title);
            Console.WriteLine();
        }

        /// <summary>
        /// Adds a product to the specified account.
        /// </summary>
        /// <returns>The inserted product</returns>
        private Product InsertProduct(ulong merchantId, string websiteUrl)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Inserting a product");
            Console.WriteLine("=================================================================");
            Product product = GenerateProduct(websiteUrl);

            Product response = service.Products.Insert(product, merchantId).Execute();
            Console.WriteLine(
                "Product inserted with ID \"{0}\" and title \"{1}\".",
                response.Id,
                response.Title);
            Console.WriteLine();
            return response;
        }

        /// <summary>
        /// Deletes a product from the specified account.
        /// </summary>
        private void DeleteProduct(ulong merchantId, Product product)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Deleting product {0}", product.Id));
            Console.WriteLine("=================================================================");

            service.Products.Delete(merchantId, product.Id).Execute();

            Console.WriteLine(
                "Product with ID \"{0}\" and title \"{1}\" was deleted.",
                product.Id,
                product.Title);
            Console.WriteLine();
        }

        /// <summary>
        /// Inserts several products to the specified account, using custombatch.
        /// </summary>
        /// <returns>The list of product IDs, which can be used to get or delete them.</returns>
        private List<String> InsertProductCustombatch(ulong merchantId, string websiteUrl)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Inserting products using custombatch");
            Console.WriteLine("=================================================================");

            ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
            batchRequest.Entries = new List<ProductsCustomBatchRequestEntry>();
            for (int i = 0; i < 3; i++)
            {
                ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
                entry.BatchId = i;
                entry.MerchantId = merchantId;
                entry.Method = "insert";
                entry.Product = GenerateProduct(websiteUrl);
                batchRequest.Entries.Add(entry);
            }

            ProductsCustomBatchResponse response = service.Products.Custombatch(batchRequest).Execute();
            List<String> productsInserted = new List<String>();
            if (response.Kind == "content#productsCustomBatchResponse")
            {
                for (int i = 0; i < response.Entries.Count; i++)
                {
                    Product product = response.Entries[i].Product;
                    productsInserted.Add(product.Id);
                    Console.WriteLine(
                        "Product inserted with ID \"{0}\" and title \"{1}\".",
                        product.OfferId,
                        product.Title);
                }
            }
            else {
                Console.WriteLine(
                    "There was an error. Response: {0}",
                    response.ToString());
            }
            return productsInserted;

        }

        /// <summary>
        /// Deletes several products from the specified account, using custombatch.
        /// </summary>
        private void DeleteProductCustombatch(ulong merchantId, List<String> productList)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Deleting products using custombatch");
            Console.WriteLine("=================================================================");

            ProductsCustomBatchRequest batchRequest = new ProductsCustomBatchRequest();
            batchRequest.Entries = new List<ProductsCustomBatchRequestEntry>();
            for (int i = 0; i < productList.Count; i++)
            {
                ProductsCustomBatchRequestEntry entry = new ProductsCustomBatchRequestEntry();
                entry.BatchId = i;
                entry.MerchantId = merchantId;
                entry.Method = "delete";
                entry.ProductId = productList[i]; // Use the full product ID here, not the OfferId
                batchRequest.Entries.Add(entry);
            }

            ProductsCustomBatchResponse response = service.Products.Custombatch(batchRequest).Execute();
            if (response.Kind == "content#productsCustomBatchResponse")
            {
                for (int i = 0; i < response.Entries.Count; i++)
                {
                    Errors errors = response.Entries[i].Errors;
                    if (errors != null)
                    {
                        for (int j = 0; j < errors.ErrorsValue.Count; j++)
                        {
                            Console.WriteLine(errors.ErrorsValue[j].ToString());
                        }
                    }
                    else {
                        Console.WriteLine("Product deleted, batchId {0}", response.Entries[i].BatchId);
                    }
                }
            }
            else {
                Console.WriteLine(
                    "There was an error. Response: {0}",
                    response);
            }
        }

        internal Product GenerateProduct(string websiteUrl)
        {
              Product product = new Product() {
              OfferId = String.Format("product#{0}", shoppingUtil.GetUniqueId()),
              Title = "A Tale of Two Cities",
              Description = "A classic novel about the French Revolution",
              Link = $"{websiteUrl}/tale-of-two-cities.html",
              ImageLink = $"{websiteUrl}/tale-of-two-cities.jpg",
              ContentLanguage = "EN",
              TargetCountry = "US",
              Channel = "online",
              Availability = "in stock",
              Condition = "new",
              GoogleProductCategory = "Media > Books",
              Gtin = "9780007350896",
              Price = new Price() {
                Currency = "USD",
                Value = "2.50"
              },
              Shipping = new List<ProductShipping> {
                new ProductShipping() {
                  Country = "US",
                  Service = "Standard shipping",
                  Price = new Price() {
                    Currency = "USD",
                    Value = "0.99"
                  }
                }
              },
              ShippingWeight = new ProductShippingWeight() {
                Unit = "grams",
                Value = 200
              }
            };

            return product;
        }
    }
}
