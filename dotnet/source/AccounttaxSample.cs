using System;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.ShoppingContent.v2_1.Data;
using System.Collections.Generic;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs multiple requests against the Accounttax
    /// service in the Content API for Shopping.  It also exports some
    /// auxilliary functions for the main program.
    /// <para>These include:
    /// <list type="bullet">
    /// <item>
    /// <description>Accounttax.get</description>
    /// </item>
    /// <item>
    /// <description>Accounttax.update</description>
    /// </item></list></para>
    /// </summary>
    public class AccounttaxSample
    {
        private ShoppingContentService service;

        /// <summary>Initializes a new instance of the <see cref="AccounttaxSample"/> class.</summary>
        /// <param name="service">Content service object on which to run the requests.</param>
        public AccounttaxSample(ShoppingContentService service)
        {
            this.service = service;
        }

        /// <summary>Runs requests against the Content API for Shopping.</summary>
        internal void RunCalls(ulong merchantId)
        {
            AccountTax oldSettings = GetAccountTax(merchantId, merchantId);
            AccountTax newSettings = SampleTaxSettings(merchantId);
            UpdateAccountTax(merchantId, merchantId, newSettings);
            GetAccountTax(merchantId, merchantId);
            oldSettings.ETag = null; // Clear out the ETag first.
            UpdateAccountTax(merchantId, merchantId, oldSettings);
            GetAccountTax(merchantId, merchantId);
        }

        /// <summary>
        /// Retrieves the tax settings for a particular account.
        /// </summary>
        /// <returns>The tax settings for the specified account.</returns>
        private AccountTax GetAccountTax(ulong merchantId, ulong accountId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Account Tax Settings for {0}", accountId);
            Console.WriteLine("=================================================================");

            AccountTax settings = service.Accounttax.Get(merchantId, accountId).Execute();
            PrintAccountTax(settings);
            Console.WriteLine();

            return settings;
        }

        private void UpdateAccountTax(ulong merchantId, ulong accountId, AccountTax settings)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Updating Account Tax Settings for {0}", accountId);
            Console.WriteLine("=================================================================");

            service.Accounttax.Update(settings, merchantId, accountId).Execute();
            Console.WriteLine();
        }

        private AccountTax SampleTaxSettings(ulong accountId)
        {
            AccountTaxTaxRule taxNY = new AccountTaxTaxRule {
              Country = "US",
              LocationId = 21167,
              UseGlobalRate = true
            };

            AccountTax settings = new AccountTax() {
              AccountId = accountId,
              Rules = new List<AccountTaxTaxRule> { taxNY }
            };
            return settings;
        }

        private void PrintAccountTax(AccountTax settings)
        {
            Console.WriteLine("Tax settings for account {0}:", settings.AccountId);
            if (settings.Rules == null || settings.Rules.Count == 0)
            {
                Console.WriteLine("No tax rules.");
            }
            else
            {
                foreach (var rule in settings.Rules)
                {
                    Console.Write("For location {0} in country {0}: ", rule.LocationId, rule.Country);
                    if (rule.RatePercent != null)
                    {
                        Console.WriteLine("tax is {0}%.", rule.RatePercent);
                    }
                    if (rule.UseGlobalRate == true)
                    {
                        Console.WriteLine("using the global tax table rate.");
                    }
                    if (rule.ShippingTaxed == true)
                    {
                        Console.WriteLine(" Note: Shipping charges are also taxed.");
                    }
                }
            }
        }
    }
}

