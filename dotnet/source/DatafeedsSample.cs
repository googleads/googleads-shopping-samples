using System;
using System.Collections.Generic;
using System.Net;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Datafeeds
    /// service in the Content API for Shopping.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Datafeeds.list</description>
    /// </item>
    /// <item>
    /// <description>Datafeeds.insert</description>
    /// </item>
    /// <item>
    /// <description>Datafeeds.update</description>
    /// </item>
    /// <item>
    /// <description>Datafeeds.delete</description>
    /// </item>
    /// </list></para>
    /// </summary>
    public class DatafeedsSample
    {
        private ShoppingContentService service;
        ShoppingUtil shoppingUtil = new ShoppingUtil();
        // Currently, we may receive a 401 Unauthorized error if a datafeed is not yet
        // available soon after creating it, so retry if we see one while making a modification
        // to or deleting a datafeed. The specific HTTP error we receive may be subject to change.
        IEnumerable<HttpStatusCode> retryCodes = new HttpStatusCode[] {HttpStatusCode.Unauthorized};

        /// <summary>Initializes a new instance of the <see cref="DatafeedsSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        public DatafeedsSample(ShoppingContentService service)
        {
            this.service = service;
        }

        /// <summary>Runs multiple requests against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId)
        {
            // Datafeeds
            GetAllDatafeeds(merchantId);
            Datafeed newDatafeed = InsertDatafeed(merchantId);
            UpdateDatafeed(merchantId, (ulong)newDatafeed.Id);
            DeleteDatafeed(merchantId, (ulong)newDatafeed.Id);
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
        /// Updates a datafeed using the Datafeeds.update method.
        /// </summary>
        private void UpdateDatafeed(ulong merchantId, ulong datafeedId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine(String.Format("Updating datafeed {0}", datafeedId));
            Console.WriteLine("=================================================================");

            Datafeed datafeed = service.Datafeeds.Get(merchantId, datafeedId).Execute();
            datafeed.FetchSchedule.Hour = 7;

            // Set ETag to null as Update() will reject it otherwise.
            datafeed.ETag = null;

            var request = service.Datafeeds.Update(datafeed, merchantId, datafeedId);
            Datafeed response = shoppingUtil.ExecuteWithRetries(request, retryCodes);
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
            Datafeed datafeed = GenerateDatafeed();

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

            var request = service.Datafeeds.Delete(merchantId, datafeedId);
            shoppingUtil.ExecuteWithRetries(request, retryCodes);

            Console.WriteLine("Datafeed with ID \"{0}\" was deleted.", datafeedId);
            Console.WriteLine();
        }

        internal Datafeed GenerateDatafeed()
        {
            String name = String.Format("datafeed{0}", shoppingUtil.GetUniqueId());
            Datafeed datafeed = new Datafeed
            {
              Name = name,
              ContentType = "products",
              AttributeLanguage = "en",
              FileName = name,
              FetchSchedule = new DatafeedFetchSchedule {
                Weekday = "monday",
                Hour = 6,
                TimeZone = "America/Los_Angeles",
                FetchUrl = $"http://feeds.my-shop.com/{name}"
              },
              Format = new DatafeedFormat {
                FileEncoding = "utf-8",
                ColumnDelimiter = "tab",
                QuotingMode = "value quoting"
              },
              Targets = new List<DatafeedTarget> {
                new DatafeedTarget {
                  Country = "US",
                  Language = "EN",
                  IncludedDestinations = new List<String> { "Shopping" }
                }
              }
            };

            return datafeed;
        }
    }
}

