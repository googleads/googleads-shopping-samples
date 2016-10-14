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

func productDemo(ctx context.Context, service *content.APIService, merchantID uint64) {
	offerID := fmt.Sprintf("book#test%d", rand.Int())
	product := createSampleProduct(offerID)

	products := content.NewProductsService(service)

	fmt.Printf("Inserting product with offerId %s... ", offerID)
	productInfo, err := products.Insert(merchantID, product).Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	checkContentErrors(productInfo.Warnings, false)
	productID := productInfo.Id

	fmt.Printf("Listing products:\n")
	listCall := products.List(merchantID)
	// Uncomment this to see even invalid offers:
	//   listCall.IncludeInvalidInsertedItems(true)
	// Uncomment this to change the number of results listed by
	// per page:
	//   listCall.MaxResults(100)
	err = listCall.Pages(ctx, printProductsPage)
	checkAPI(err, "Listing products failed")
	fmt.Printf("\n")

	fmt.Printf("Retrieving product ID %s...", productID)
	productInfo, err = products.Get(merchantID, productID).Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.\n")
	fmt.Printf("Retrieved product %s with title %s\n",
		productInfo.Id, productInfo.Title)

	fmt.Printf("Deleting product ID %s...", productID)
	err = products.Delete(merchantID, productID).Do()
	checkAPI(err, "Deletion failed")
	fmt.Printf("done.\n")
}

func printProductsPage(res *content.ProductsListResponse) error {
	for _, product := range res.Resources {
		fmt.Printf(" - Offer %s: %s\n",
			product.OfferId, product.Title)
	}
	return nil
}

func createSampleProduct(offerID string) *content.Product {
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
		Link:                  "http://my-book-shop.com/tale-of-two-cities.html",
		ImageLink:             "http://my-book-shop.com/tale-of-two-cities.jpg",
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
