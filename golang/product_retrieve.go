package main

// This file contains a demo of using the Product service by
// retrieving product list

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2.1"
)

func productsRetrieveDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	if config.WebsiteURL == "" {
		fmt.Println("This demo requires the account to have a configured website.")
		return
	}

	products := content.NewProductsService(service)

	fmt.Printf("Listing products:\n")
	listCall := products.List(config.MerchantID)
	// Enable this to change the number of results listed by
	// per page:
	if false {
		listCall.MaxResults(100)
	}
	if err := listCall.Pages(ctx, printProductsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing products failed")
	}
	fmt.Printf("\n")
}

func printProductsPage(res *content.ProductsListResponse) error {
	for _, product := range res.Resources {
		fmt.Printf(" - Offer %s: %s\n",
			product.OfferId, product.Title)
	}
	return nil
}
