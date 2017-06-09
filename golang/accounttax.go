package main

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func accountTaxDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		multiClientAccountTaxDemo(ctx, service, config)
	} else {
		primaryAccountTaxDemo(service, config)
	}
}

func multiClientAccountTaxDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if !config.IsMCA {
		fmt.Println("This demo must be run on a multi-client account.")
		return
	}
	accountTax := content.NewAccounttaxService(service)

	fmt.Printf("Printing tax settings of subaccounts of %d:\n", config.MerchantID)
	if err := accountTax.List(config.MerchantID).Pages(ctx, printAccountTaxPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccount tax settings failed")
	}
}

// This function runs a demo of the Accounttax service by retrieving
// the current tax settings for the account, setting the tax settings with
// specific examples, and then replacing the old settings afterwards, printing
// out the current settings at each step.
func primaryAccountTaxDemo(service *content.APIService, config *merchantInfo) {
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

	fmt.Print("Changing original tax settings via update... ")
	newSettings := createSimpleTaxSample(config)
	settings, err := accountTax.Update(config.MerchantID, config.MerchantID, newSettings).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating tax settings failed")
	}
	fmt.Println("done.")
	printTaxSettings(settings)
	fmt.Println()

	fmt.Print("Changing original tax settings via patch... ")
	newSettings = createSimpleTaxPatch(settings)
	settings, err = accountTax.Patch(config.MerchantID, config.MerchantID, newSettings).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Patching tax settings failed")
	}
	fmt.Println("done.")
	printTaxSettings(settings)
	fmt.Println()

	fmt.Print("Replacing original sample tax settings... ")
	settings, err = accountTax.Update(config.MerchantID, config.MerchantID, oldSettings).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Replacing original tax settings failed")
	}
	fmt.Println("done.")
	printTaxSettings(settings)
}

func printAccountTaxPage(res *content.AccounttaxListResponse) error {
	for _, as := range res.Resources {
		printTaxSettings(as)
	}
	return nil
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

func createSimpleTaxSample(config *merchantInfo) *content.AccountTax {
	return &content.AccountTax{
		AccountId: config.MerchantID,
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
