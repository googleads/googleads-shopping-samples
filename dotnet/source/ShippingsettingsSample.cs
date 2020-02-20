using System;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;
using System.Collections.Generic;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Shippingsettings
    /// service in the Content API for Shopping.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Shippingsettings.get</description>
    /// </item>
    /// <item>
    /// <description>Shippingsettings.update</description>
    /// </item></list></para>
    /// </summary>
    public class ShippingsettingsSample
    {
        private ShoppingContentService service;

        /// <summary>Initializes a new instance of the <see cref="ShippingsettingsSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        public ShippingsettingsSample(ShoppingContentService service)
        {
            this.service = service;
        }

        /// <summary>Runs requests against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId)
        {
            ShippingSettings oldSettings = GetShippingSettings(merchantId, merchantId);
            ShippingSettings newSettings = SampleShippingSettings();
            UpdateShippingSettings(merchantId, merchantId, newSettings);
            GetShippingSettings(merchantId, merchantId);
            oldSettings.ETag = null; // Clear out the ETag first.
            UpdateShippingSettings(merchantId, merchantId, oldSettings);
            GetShippingSettings(merchantId, merchantId);
        }

        /// <summary>
        /// Retrieves the shipping settings for a particular account.
        /// </summary>
        /// <returns>The shipping settings for the specified account.</returns>
        private ShippingSettings GetShippingSettings(ulong merchantId, ulong accountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Account Shipping Settings for {0}", accountId);
            Console.WriteLine("=================================================================");

            ShippingSettings settings = service.Shippingsettings.Get(merchantId, accountId).Execute();
            PrintShippingSettings(settings);
            Console.WriteLine();

            return settings;
        }

        private void UpdateShippingSettings(ulong merchantId, ulong accountId, ShippingSettings settings)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Updating Account Shipping Settings for {0}", accountId);
            Console.WriteLine("=================================================================");

            service.Shippingsettings.Update(settings, merchantId, accountId).Execute();
            Console.WriteLine();
        }

        private ShippingSettings SampleShippingSettings()
        {
            RateGroup rateUSPS = new RateGroup();
            rateUSPS.ApplicableShippingLabels = new List<string>();
            rateUSPS.SingleValue = new Value() {
              FlatRate = new Price() {
                Value = "5.00",
                Currency = "USD"
              }
            };

            Service serviceUSPS = new Service() {
              Name = "USPS",
              Currency = "USD",
              DeliveryCountry = "US",
              DeliveryTime = new DeliveryTime() {
                MinTransitTimeInDays = 3,
                MaxTransitTimeInDays = 7,
              },
              RateGroups = new List<RateGroup> { rateUSPS },
              Active = true
            };

            ShippingSettings settings = new ShippingSettings() {
              PostalCodeGroups = new List<PostalCodeGroup>(),
              Services = new List<Service> { serviceUSPS }
            };
            return settings;
        }

        private void PrintShippingSettings(ShippingSettings settings)
        {
            Console.WriteLine("Shipping settings for account {0}:", settings.AccountId);
            if (settings.PostalCodeGroups == null || settings.PostalCodeGroups.Count == 0)
            {
                Console.WriteLine("- No postal code groups.");
            }
            else
            {
                Console.WriteLine("- {0} postal code group(s):", settings.PostalCodeGroups.Count);
                foreach (var group in settings.PostalCodeGroups)
                {
                    Console.WriteLine("  Postal code group \"{0}\":", group.Name);
                    Console.WriteLine("  - Country: {0}", group.Country);
                    Console.WriteLine("  - Contains {0} postal code ranges.", group.PostalCodeRanges.Count);
                }
            }
            if (settings.Services == null || settings.Services.Count == 0)
            {
                Console.WriteLine("- No shipping services.");
            }
            else
            {
                Console.WriteLine("- {0} shipping service(s):", settings.Services.Count);
                foreach (var service in settings.Services)
                {
                    Console.WriteLine("  Service \"{0}\":", service.Name);
                    Console.WriteLine("  - Active: {0}", service.Active);
                    Console.WriteLine("  - Country: {0}", service.DeliveryCountry);
                    Console.WriteLine("  - Currency: {0}", service.Currency);
                    Console.WriteLine("  - Delivery time: {0} - {1} days",
                        service.DeliveryTime.MinTransitTimeInDays,
                        service.DeliveryTime.MaxTransitTimeInDays);
                    Console.WriteLine("  - {0} rate group(s) in this service.", service.RateGroups.Count);
                }
            }
        }
    }
}
