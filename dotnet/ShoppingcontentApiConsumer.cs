using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

using Google.Apis.Requests;
using Google.Apis.ShoppingContent.v2;
using Google.Apis.ShoppingContent.v2.Data;

namespace ContentShoppingSamples
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Content API for Shopping.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Product Create/Read/Update/Delete</description>
    /// </item>
    /// <item>
    /// <description>Product Create/Delete in batches</description>
    /// </item>
    /// <item>
    /// <description>Datafeeds Create/Read/Update/Delete</description>
    /// </item>
    /// <item>
    /// <description>Primary account user Create/Delete</description>
    /// </item>
    /// </list></para>
    /// </summary>
    class ShoppingContentApiConsumer
    {
        private ShoppingContentService service;
        private int maxListPageSize;
        ShoppingUtil shoppingUtil = new ShoppingUtil();

        /// <summary>Initializes a new instance of the <see cref="ShoppingcontentApiConsumer"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        /// <param name="maxListPageSize">The maximum page size to retrieve.</param>
        public ShoppingContentApiConsumer(ShoppingContentService service, int maxListPageSize)
        {
            this.service = service;
            this.maxListPageSize = maxListPageSize;
        }

        /// <summary>Runs multiple requests against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId, String emailAddress="", ulong adWordsAccountId=0)
        {
            // Products
            GetAllProducts(merchantId);
            Product newProduct = InsertProduct(merchantId);
            UpdateProduct(merchantId, newProduct.Id);
            UpdateProductUsingInventory(merchantId, newProduct.Id);
            DeleteProduct(merchantId, newProduct);

            // Products - batches
            List<String> productList = InsertProductCustombatch(merchantId);
            DeleteProductCustombatch(merchantId, productList);

            // Datafeeds
            var datafeed = GetAllDatafeeds(merchantId);
            Datafeed newDatafeed = InsertDatafeed(merchantId);
            UpdateDatafeed(merchantId, (ulong)newDatafeed.Id);
            try
            {
                DeleteDatafeed(merchantId, (ulong)newDatafeed.Id);
            }
            catch (Google.GoogleApiException e)
            {
                Console.WriteLine("Warning: Tried to delete a datafeed too soon after creation. " + e.Message);
            }

            // Primary account, user management
            if (emailAddress != "")
            {
                AccountUser user = AddUser(merchantId, merchantId, emailAddress);
                RemoveUser(merchantId, merchantId, user.EmailAddress);
            }

            // Primary account, AdWords account link
            if (adWordsAccountId != 0)
            {
                LinkAdWordsAccount(merchantId, adWordsAccountId);
                UnlinkAdWordsAccount(merchantId, adWordsAccountId);
            }

        }

        internal void RunMultiClientAccountCalls(ulong merchantId)
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
            try {
                DeleteAccount(merchantId, (ulong)newAccount.Id);
            }
            catch (Google.GoogleApiException e)
            {
                Console.WriteLine("Warning: Tried to delete an account too soon after creation. " + e.Message);
            }
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
                else
                {
                    Console.WriteLine("No accounts found.");
                }

                pageToken = productsResponse.NextPageToken;
            }
            while (pageToken != null);
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
            // products collection in Content API v2.

            Product product = service.Products.Get(merchantId, productId).Execute();

            // Set ETag to null as Insert() will reject it otherwise.
            product.ETag = null;

            product.ProductType = "English/Classics";

            Product response = service.Products.Insert(product, merchantId).Execute();
            Console.WriteLine(
                "Product updated with ID \"{0}\" and title \"{1}\".",
                response.Id,
                response.Title);
            Console.WriteLine();
        }

        /// <summary>
        /// Updates the specified product on the specified account using the inventory collection.
        /// <para>If you're updating any of the supported properties in a product, be sure to use the inventory.set
        /// method, for performance reasons.</para>
        /// </summary>
        private void UpdateProductUsingInventory(ulong merchantId, String productId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Updating product using Inventory {0}", productId));
            Console.WriteLine("=================================================================");

            InventorySetRequest body = new InventorySetRequest();
            body.Availability = "out of stock";
            body.Price = new Price();

            body.Price.Currency = "USD";
            body.Price.Value = "3.00";

            InventorySetResponse response =
                service.Inventory.Set(body, merchantId, productId.Split(':').First(), productId).Execute();
            Console.WriteLine("Product updated with ID \"{0}\".", productId);
            Console.WriteLine();
        }

        /// <summary>
        /// Adds a product to the specified account.
        /// </summary>
        /// <returns>The inserted product</returns>
        private Product InsertProduct(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Inserting a product");
            Console.WriteLine("=================================================================");
            Product product = shoppingUtil.GenerateProduct();

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
        private List<String> InsertProductCustombatch(ulong merchantId)
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
                entry.Product = shoppingUtil.GenerateProduct();
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
            else
            {
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
                    else
                    {
                        Console.WriteLine("Product deleted, batchId {0}", response.Entries[i].BatchId);
                    }
                }
            }
            else
            {
                Console.WriteLine(
                    "There was an error. Response: {0}",
                    response);
            }
        }

        /// <summary>Gets and prints all datafeeds for the given merchant ID.</summary>
        /// <returns>The last page of retrieved accounts.</returns>
        private DatafeedsListResponse GetAllDatafeeds(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing all Datafeeds");
            Console.WriteLine("=================================================================");

            // Retrieve account list in pages and display data as we receive it.
            DatafeedsListResponse datafeedsResponse = null;

            DatafeedsResource.ListRequest accountRequest = service.Datafeeds.List(merchantId);
            datafeedsResponse = accountRequest.Execute();

            if (datafeedsResponse.Resources != null && datafeedsResponse.Resources.Count != 0)
            {
                foreach (var datafeed in datafeedsResponse.Resources)
                {
                    Console.WriteLine(
                        "Datafeed with ID \"{0}\" and name \"{1}\" was found.",
                        datafeed.Id,
                        datafeed.Name);
                }
            }
            else
            {
                Console.WriteLine("No accounts found.");
            }

            Console.WriteLine();

            // Return the last page of accounts.
            return datafeedsResponse;
        }

        /// <summary>
        /// Updates a datafeed using the datafeeds.insert method as there is no update method.
        /// <para>Inserting a datafeed with an ID that already exists means the same as doing an update.</para>
        /// </summary>
        private void UpdateDatafeed(ulong merchantId, ulong datafeedId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Updating datafeed {0}", datafeedId));
            Console.WriteLine("=================================================================");
            // First we need to retrieve the full object, since there are no partial updates for the
            // datafeeds collection in Content API v2.

            Datafeed datafeed = service.Datafeeds.Get(merchantId, datafeedId).Execute();

            // Set ETag to null as Patch() will reject it otherwise.
            datafeed.ETag = null;

            datafeed.FetchSchedule.Hour = 7;

            Datafeed response = service.Datafeeds.Patch(datafeed, merchantId, datafeedId).Execute();
            Console.WriteLine(
                "Datafeed updated with ID \"{0}\" and name \"{1}\".",
                response.Id,
                response.Name);
            Console.WriteLine();
        }

        /// <summary>
        /// Adds a datafeed to the specified account.
        /// </summary>
        /// <returns>The datafeed that was inserter</returns>
        private Datafeed InsertDatafeed(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Inserting a datafeed");
            Console.WriteLine("=================================================================");
            Datafeed datafeed = shoppingUtil.GenerateDatafeed();

            Datafeed response = service.Datafeeds.Insert(datafeed, merchantId).Execute();
            Console.WriteLine(
                "Datafeed inserted with ID \"{0}\" and name \"{1}\".",
                response.Id,
                response.Name);
            Console.WriteLine();
            return response;
        }

        /// <summary>
        /// Removes a datafeed from the specified account.
        /// </summary>
        private void DeleteDatafeed(ulong merchantId, ulong datafeedId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Deleting datafeed {0}", datafeedId));
            Console.WriteLine("=================================================================");

            service.Datafeeds.Delete(merchantId, datafeedId).Execute();

            Console.WriteLine("Datafeed with ID \"{0}\" was deleted.", datafeedId);
            Console.WriteLine();
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

            if (account.AdwordsLinks == null) {
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
            else
            {
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
            Account account = shoppingUtil.GenerateAccount();

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
    }
}
