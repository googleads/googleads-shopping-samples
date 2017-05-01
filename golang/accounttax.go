package main

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

// This function runs a demo of the Accounttax service by retrieving
// the current tax settings for the account, setting the tax settings with a
// specific example, and then replacing the old settings afterwards, printing
// out the current settings at each step.
func accountTaxDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	accountTax := content.NewAccounttaxService(service)

	fmt.Println("Retrieving original tax settings:")
	oldSettings, err := accountTax.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving original tax settings failed")
	}
	printTaxSettings(oldSettings)
	fmt.Println()

	fmt.Println("Retrieving current tax settings:")
	settings, err := accountTax.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving current tax settings failed")
	}
	printTaxSettings(settings)
	fmt.Println()

	fmt.Print("Replacing original sample tax settings... ")
	if _, err := accountTax.Update(config.MerchantID, config.MerchantID, oldSettings).Do(); err != nil {
		dumpAPIErrorAndStop(err, "Replacing original tax settings failed")
	}
	fmt.Println("done.")

	fmt.Println("Retrieving current tax settings:")
	settings, err = accountTax.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving current tax settings failed")
	}
	printTaxSettings(settings)
	fmt.Println()
}

func printTaxSettings(res *content.AccountTax) {
	fmt.Printf("Tax settings for account %d:\n", res.AccountId)
	if len(res.Rules) == 0 {
		fmt.Println("  No tax charged.")
		return
	}
	for n, rule := range res.Rules {
		fmt.Printf("  Rule %d:\n", n)
		fmt.Printf("  - Country: %s\n", rule.Country)
		fmt.Printf("  - Location ID: %d\n", rule.LocationId)
		if rule.RatePercent != "" {
			fmt.Printf("  - Rate percent: %s%%\n", rule.RatePercent)
		}
		if rule.ShippingTaxed {
			fmt.Println("  - Shipping charges also taxed.")
		}
		if rule.UseGlobalRate {
			fmt.Println("  - Using global tax table.")
		}
	}
}

func createSimpleTaxSample() *content.AccountTax {
	return &content.AccountTax{
		Rules: []*content.AccountTaxTaxRule{
			&content.AccountTaxTaxRule{
				Country:       "US",
				LocationId:    21167,
				UseGlobalRate: true,
			},
		},
	}
}

func createSimpleTaxPatch(res *content.AccountTax) *content.AccountTax {
	return &content.AccountTax{
		Rules: append(res.Rules, &content.AccountTaxTaxRule{
			Country:     "US",
			LocationId:  21143,
			RatePercent: "6",
		}),
	}
}
