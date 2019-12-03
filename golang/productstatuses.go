package main

// This file contains a demo of using the Productstatuses service by
// retrieving the current list of products and printing out any data
// quality issues (if any) for each product.

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2.1"
)

func productstatusesDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	productstatuses := content.NewProductstatusesService(service)

	fmt.Printf("Listing product statuses:\n")
	listCall := productstatuses.List(config.MerchantID)
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
		fmt.Printf(" - Destination statuses for offer %s\n", productstatus.ProductId)
		for _, ds := range productstatus.DestinationStatuses {
			fmt.Printf("\t%s for destination %q", ds.Status, ds.Destination)
			if ds.Status == "pending" {
				fmt.Printf(" (still pending)\n")
			} else {
				fmt.Printf("\n")
			}
		}
		fmt.Printf(" - Issues for offer %s\n", productstatus.ProductId)
		for _, ili := range productstatus.ItemLevelIssues {
			fmt.Printf("\t- Code: %s\n", ili.Code)
			fmt.Printf("\t  Description: %s\n", ili.Description)
			fmt.Printf("\t  Detailed description: %s\n", ili.Detail)
			fmt.Printf("\t  Resolution: %s\n", ili.Resolution)
			fmt.Printf("\t  Servability: %s\n", ili.Servability)
		}
		fmt.Printf("\n")
	}
	return nil
}
