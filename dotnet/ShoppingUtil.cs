using System;
using System.Collections.Generic;

using Google.Apis.ShoppingContent.v2.Data;

namespace ContentShoppingSamples
{
    class ShoppingUtil
    {
        private int unique_id_increment = 0;

        /// <summary>
        /// Generates a unique ID based on the UNIX timestamp and a runtime increment.
        /// </summary>
        internal String GetUniqueId()
        {
            unique_id_increment += 1;
            String unixTimestamp =
                ((Int32)(DateTime.UtcNow.Subtract(new DateTime(1970, 1, 1))).TotalSeconds).ToString();
            return unixTimestamp + unique_id_increment.ToString();
        }

        internal Product GenerateProduct()
        {
            Product product = new Product();
            product.OfferId = String.Format("product#{0}", GetUniqueId());
            product.Title = "A Tale of Two Cities";
            product.Description = "A classic novel about the French Revolution";
            product.Link = "http://my-book-shop.com/tale-of-two-cities.html";
            product.ImageLink = "http://my-book-shop.com/tale-of-two-cities.jpg";
            product.ContentLanguage = "EN";
            product.TargetCountry = "US";
            product.Channel = "online";
            product.Availability = "in stock";
            product.Condition = "new";
            product.GoogleProductCategory = "Media > Books";
            product.Gtin = "9780007350896";
            product.Price = new Price();
            product.Price.Currency = "USD";
            product.Price.Value = "2.50";

            ProductShipping shipping = new ProductShipping();
            shipping.Country = "US";
            shipping.Service = "Standard shipping";
            product.Shipping = new List<ProductShipping>();
            shipping.Price = new Price();
            shipping.Price.Currency = "USD";
            shipping.Price.Value = "0.99";
            product.Shipping.Add(shipping);

            product.ShippingWeight = new ProductShippingWeight();
            product.ShippingWeight.Unit = "grams";
            product.ShippingWeight.Value = 200;

            return product;
        }

        internal Datafeed GenerateDatafeed()
        {
            String name = String.Format("datafeed{0}", GetUniqueId());
            Datafeed datafeed = new Datafeed();
            datafeed.Name = name;
            datafeed.ContentType = "products";
            datafeed.AttributeLanguage = "en";
            datafeed.ContentLanguage = "EN";
            datafeed.IntendedDestinations = new List<String>();
            datafeed.IntendedDestinations.Add("Shopping");
            datafeed.FileName = name;
            datafeed.TargetCountry = "US";
            datafeed.FetchSchedule = new DatafeedFetchSchedule();
            datafeed.FetchSchedule.Weekday = "monday";
            datafeed.FetchSchedule.Hour = 6;
            datafeed.FetchSchedule.TimeZone = "America/Los_Angeles";
            datafeed.FetchSchedule.FetchUrl = "http://feeds.my-shop.com/" + name;
            datafeed.Format = new DatafeedFormat();
            datafeed.Format.FileEncoding = "utf-8";
            datafeed.Format.ColumnDelimiter = "tab";
            datafeed.Format.QuotingMode = "value quoting";

            return datafeed;
        }

        internal Account GenerateAccount()
        {
            String name = String.Format("account{0}", GetUniqueId());
            Account account = new Account();
            account.Name = name;
            account.WebsiteUrl = String.Format("https://{0}.example.com/", name);
            return account;
        }
    }
}
