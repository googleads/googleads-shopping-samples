package main

// This file contains a demo of using the Productstatuses service by
// retrieving the current list of products and printing out any data
// quality issues (if any) for each product.

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func productstatusesDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	productstatuses := content.NewProductstatusesService(service)

	fmt.Printf("Listing product statuses:\n")
	listCall := productstatuses.List(config.MerchantID)
	// Enable this to see even invalid offers:
	if false {
		listCall.IncludeInvalidInsertedItems(true)
	}
	// Enable this to change the number of results listed by
	// per page:
	if false {
		listCall.MaxResults(100)
	}
	if err := listCall.Pages(ctx, printProductstatusesPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing product statuses failed")
	}
	fmt.Printf("\n")
}

func printProductstatusesPage(res *content.ProductstatusesListResponse) error {
	for _, productstatus := range res.Resources {
		fmt.Printf(" - Offer %s\n",
			productstatus.ProductId)
		for _, dqi := range productstatus.DataQualityIssues {
			fmt.Printf("\t(%s) %s [%s]: %s\n",
				dqi.Severity, dqi.Id, dqi.Location, dqi.Detail)
		}
		fmt.Printf("\n")
	}
	return nil
}
