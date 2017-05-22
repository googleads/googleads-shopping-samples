package main

// This file contains a demo of using the Product service by creating a
// sample product with a random offerId, inserting it, and then
// retrieving it (to show that it was indeed inserted).

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func productDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	if config.WebsiteURL == "" {
		fmt.Println("This demo requires the account to have a configured website.")
		return
	}
	offerID := fmt.Sprintf("book#test%d", rand.Int())
	product := createSampleProduct(config, offerID)

	products := content.NewProductsService(service)

	fmt.Printf("Inserting product with offerId %s... ", offerID)
	productInfo, err := products.Insert(config.MerchantID, product).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Insertion failed")
	}
	fmt.Printf("done.\n")
	checkContentErrors(productInfo.Warnings, false)
	productID := productInfo.Id

	fmt.Printf("Listing products:\n")
	listCall := products.List(config.MerchantID)
	// Enable this to see even invalid offers:
	if false {
		listCall.IncludeInvalidInsertedItems(true)
	}
	// Enable this to change the number of results listed by
	// per page:
	if false {
		listCall.MaxResults(100)
	}
	if err := listCall.Pages(ctx, printProductsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing products failed")
	}
	fmt.Printf("\n")

	fmt.Printf("Retrieving product ID %s...", productID)
	productInfo, err = products.Get(config.MerchantID, productID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieval failed")
	}
	fmt.Printf("done.\n")
	fmt.Printf("Retrieved product %s with title %s\n",
		productInfo.Id, productInfo.Title)

	fmt.Printf("Deleting product ID %s...", productID)
	if err := products.Delete(config.MerchantID, productID).Do(); err != nil {
		dumpAPIErrorAndStop(err, "Deletion failed")
	}
	fmt.Printf("done.\n")
}

func printProductsPage(res *content.ProductsListResponse) error {
	for _, product := range res.Resources {
		fmt.Printf(" - Offer %s: %s\n",
			product.OfferId, product.Title)
	}
	return nil
}

func createSampleProduct(config *merchantInfo, offerID string) *content.Product {
	websiteURL := config.WebsiteURL
	if websiteURL == "" {
		websiteURL = "http://my-book-shop.com"
	}
	productPrice := content.Price{Currency: "USD", Value: "2.50"}
	shippingPrice := content.Price{Currency: "USD", Value: "0.99"}
	shippingWeight := content.ProductShippingWeight{
		Value: 200.0,
		Unit:  "grams",
	}
	shippingInfo := content.ProductShipping{
		Country: "US",
		Service: "Standard shipping",
		Price:   &shippingPrice,
	}
	product := content.Product{
		OfferId:               offerID,
		Title:                 "A Tale of Two Cities",
		Description:           "A classic novel about the French Revolution",
		Link:                  websiteURL + "/tale-of-two-cities.html",
		ImageLink:             websiteURL + "/tale-of-two-cities.jpg",
		ContentLanguage:       "en",
		TargetCountry:         "US",
		Channel:               "online",
		Availability:          "in stock",
		Condition:             "new",
		GoogleProductCategory: "Media > Books",
		Gtin:           "9780007350896",
		Price:          &productPrice,
		Shipping:       [](*content.ProductShipping){&shippingInfo},
		ShippingWeight: &shippingWeight,
	}
	return &product
}
