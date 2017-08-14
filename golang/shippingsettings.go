package main

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func shippingSettingsDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		multiClientShippingSettingsDemo(ctx, service, config)
	} else {
		primaryShippingSettingsDemo(service, config)
	}
}

func multiClientShippingSettingsDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if !config.IsMCA {
		fmt.Println("This demo must be run on a multi-client account.")
		return
	}
	shippingSettings := content.NewShippingsettingsService(service)

	fmt.Printf("Printing shipping settings of subaccounts of %d:\n", config.MerchantID)
	if err := shippingSettings.List(config.MerchantID).Pages(ctx, printShippingSettingsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccount shipping settings failed")
	}
}

// This function runs a demo of using the Shippingsettings service by
// retrieving the current shipping settings for the account,
// setting the shipping settings with a specific example, and then
// replacing the old settings afterwards, printing out the current
// settings at each step.
func primaryShippingSettingsDemo(service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	shippingSettings := content.NewShippingsettingsService(service)

	fmt.Println("Retrieving original shipping settings:")
	oldSettings, err := shippingSettings.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving original shipping settings failed")
	}
	printShippingSettings(oldSettings)
	fmt.Println()

	fmt.Println("Retrieving supported carriers:")
	resp, err := shippingSettings.Getsupportedcarriers(config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving supported carriers failed")
	}
	printSupportedCarriers(resp.Carriers)
	fmt.Println()

	fmt.Print("Setting new example shipping settings... ")
	newSettings := createSimpleSampleShippingSettings()
	settings, err := shippingSettings.Update(config.MerchantID, config.MerchantID, newSettings).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating shipping settings failed")
	}
	fmt.Println("done.")
	printShippingSettings(settings)
	fmt.Println()

	fmt.Print("Patching new example shipping settings to add postal code groups... ")
	settingsPatch := createSimpleShippingSettingsPatch()
	settings, err = shippingSettings.Patch(config.MerchantID, config.MerchantID, settingsPatch).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Patching shipping settings failed")
	}
	fmt.Println("done.")
	printShippingSettings(settings)
	fmt.Println()

	fmt.Print("Replacing original shipping settings... ")
	settings, err = shippingSettings.Update(config.MerchantID, config.MerchantID, oldSettings).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Replacing original shipping settings failed")
	}
	fmt.Println("done.")
	printShippingSettings(settings)
	fmt.Println()
}

func createSimpleSampleShippingSettings() *content.ShippingSettings {
	serviceUSPS := content.Service{
		Name:            "USPS",
		Currency:        "USD",
		DeliveryCountry: "US",
		DeliveryTime: &content.DeliveryTime{
			MinTransitTimeInDays: 3,
			MaxTransitTimeInDays: 7,
		},
		Active: true,
		RateGroups: []*content.RateGroup{
			&content.RateGroup{
				ApplicableShippingLabels: []string{},
				SingleValue: &content.Value{
					FlatRate: &content.Price{
						Value:    "5.00",
						Currency: "USD",
					},
				},
			},
		},
	}
	return &content.ShippingSettings{
		PostalCodeGroups: []*content.PostalCodeGroup{},
		Services:         []*content.Service{&serviceUSPS},
	}
}

func createSimpleShippingSettingsPatch() *content.ShippingSettings {
	northeasternUS := content.PostalCodeGroup{
		Name:    "Northeastern US",
		Country: "US",
		PostalCodeRanges: []*content.PostalCodeRange{
			&content.PostalCodeRange{
				PostalCodeRangeBegin: "0*",
			},
			&content.PostalCodeRange{
				PostalCodeRangeBegin: "1*",
			},
		},
	}
	return &content.ShippingSettings{
		PostalCodeGroups: []*content.PostalCodeGroup{
			&northeasternUS,
		},
	}
}

func printShippingSettingsPage(res *content.ShippingsettingsListResponse) error {
	for _, ss := range res.Resources {
		printShippingSettings(ss)
	}
	return nil
}

func printShippingSettings(res *content.ShippingSettings) {
	fmt.Printf("Shipping settings for account %d:\n", res.AccountId)
	fmt.Println("- Postal code groups:")
	for _, group := range res.PostalCodeGroups {
		fmt.Printf("  Postal group \"%s\":\n", group.Name)
		fmt.Printf("  - Country: %s\n", group.Country)
		for _, postalRange := range group.PostalCodeRanges {
			start := postalRange.PostalCodeRangeBegin
			end := postalRange.PostalCodeRangeEnd
			if end == "" {
				fmt.Printf("  - Postal code(s): %s\n", start)
			} else {
				fmt.Printf("  - Postal code(s): %s-%s\n", start, end)
			}
		}
	}
	fmt.Println("- Services:")
	for _, service := range res.Services {
		fmt.Printf("  Service \"%s\":\n", service.Name)
		fmt.Printf("  - Active: %t\n", service.Active)
		fmt.Printf("  - Currency: %s\n", service.Currency)
		fmt.Printf("  - Country: %s\n", service.DeliveryCountry)
		minDays := service.DeliveryTime.MinTransitTimeInDays
		maxDays := service.DeliveryTime.MaxTransitTimeInDays
		fmt.Printf("  - Delivery time: %d-%d days\n", minDays, maxDays)
		fmt.Println("  - Rate groups:")
		for n, group := range service.RateGroups {
			fmt.Printf("    Rate group %d\n", n)
			printRateGroupIndent(group, "    ")
		}
	}
}

func printRateGroup(group *content.RateGroup) {
	printRateGroupIndent(group, "")
}

func printRateGroupIndent(group *content.RateGroup, indent string) {
	fmt.Printf("%s- %d applicable shipping labels:\n", indent, len(group.ApplicableShippingLabels))
	for _, label := range group.ApplicableShippingLabels {
		fmt.Printf("%s  - \"%s\"\n", indent, label)
	}
	fmt.Printf("%s- %d carrier rate(s) listed:\n", indent, len(group.CarrierRates))
	for _, rate := range group.CarrierRates {
		fmt.Printf("%s  Carrier rate \"%s\"\n", indent, rate.Name)
		fmt.Printf("%s  - Carrier name: %s\n", indent, rate.CarrierName)
		fmt.Printf("%s  - Carrier service: %s\n", indent, rate.CarrierService)
		fmt.Printf("%s  - Origin postal code: %s\n", indent, rate.OriginPostalCode)
		if price := rate.FlatAdjustment; price != nil {
			fmt.Printf("%s  - Flat adjustment of %s %s\n", indent, price.Value, price.Currency)
		}
		if rate.PercentageAdjustment != "" {
			fmt.Printf("%s  - Percentage adjustment of %s%%\n", indent, rate.PercentageAdjustment)
		}
	}
	if group.SingleValue != nil {
		fmt.Printf("%s- Single rate value: ", indent)
		printGroupValue(group.SingleValue)
	} else if group.MainTable != nil {
		fmt.Printf("%s- Main table:\n", indent)
		printGroupTable(group.MainTable, indent+"  ")
		fmt.Printf("%s- %d defined subtable(s):\n", indent, len(group.Subtables))
		for _, subtable := range group.Subtables {
			fmt.Printf("%s  Table \"%s\"\n", indent, subtable.Name)
			printGroupTable(subtable, indent+"  ")
		}
	}
}

func printGroupTable(table *content.Table, indent string) {
	fmt.Printf("%s- Row header(s):\n", indent)
	printTableHeaders(table.RowHeaders, indent+"  ")
	if table.ColumnHeaders != nil {
		fmt.Printf("%s- Column header(s):\n", indent)
		printTableHeaders(table.ColumnHeaders, indent+"  ")
	}
	fmt.Printf("%s- Rows:\n", indent)
	for n, row := range table.Rows {
		fmt.Printf("%s  Row %d:\n", indent, n)
		for _, value := range row.Cells {
			fmt.Printf("%s  - ", indent)
			printGroupValue(value)
		}
	}
}

func printTableHeaders(headers *content.Headers, indent string) {
	if headers.Locations != nil {
		for n, loc := range headers.Locations {
			fmt.Printf("%s- Location set %d:\n", indent, n)
			for _, id := range loc.LocationIds {
				fmt.Printf("%s  - \"%s\"\n", indent, id)
			}
		}
	} else if headers.NumberOfItems != nil {
		for _, numItem := range headers.NumberOfItems {
			fmt.Printf("%s- Number of items: \"%s\"\n", indent, numItem)
		}
	} else if headers.PostalCodeGroupNames != nil {
		for _, name := range headers.PostalCodeGroupNames {
			fmt.Printf("%s- Postal code group \"%s\"\n", indent, name)
		}
	} else if headers.Prices != nil {
		for _, price := range headers.Prices {
			fmt.Printf("%s- Price <= %s %s\n", indent, price.Value, price.Currency)
		}
	} else if headers.Weights != nil {
		for _, weight := range headers.Weights {
			fmt.Printf("%s- Weight <= %s %s\n", indent, weight.Value, weight.Unit)
		}

	}
}

func printGroupValue(value *content.Value) {
	if price := value.FlatRate; price != nil {
		fmt.Printf("%s %s\n", price.Value, price.Currency)
	} else if value.PricePercentage != "" {
		fmt.Printf("%s%%\n", value.PricePercentage)
	} else if value.NoShipping {
		fmt.Println("cannot ship")
	} else if value.SubtableName != "" {
		fmt.Printf("see subtable \"%s\"\n", value.SubtableName)
	}
}

func printSupportedCarriers(carriers []*content.CarriersCarrier) {
	fmt.Println("Supported carriers:")
	for _, carrier := range carriers {
		fmt.Printf("Carrier \"%s\":\n", carrier.Name)
		fmt.Printf("- Country: %s\n", carrier.Country)
		fmt.Printf("- Has %d supported services:\n", len(carrier.Services))
		for _, service := range carrier.Services {
			fmt.Printf("  Service: \"%s\"\n", service)
		}
	}
}
