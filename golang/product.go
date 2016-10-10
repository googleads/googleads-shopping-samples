package main

// This file contains a demo of using the Product service by creating a
// sample product with a random offerId, inserting it, and then
// retrieving it (to show that it was indeed inserted).

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	content "google.golang.org/api/content/v2"
)

func productDemo(contentService *content.APIService, merchantID uint64) {
	offerID := fmt.Sprintf("book#test%d", rand.Int())
	product := createSampleProduct(offerID)

	products := content.NewProductsService(contentService)

	fmt.Printf("Inserting product with offerId %s... ", offerID)
	insertCall := products.Insert(merchantID, product)
	productInfo, err := insertCall.Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	checkContentErrors(productInfo.Warnings, false)
	productID := productInfo.Id

	fmt.Printf("Listing products:\n")
	listCall := products.List(merchantID)
	err = listCall.Pages(context.Background(), printProductsPage)
	checkAPI(err, "Listing products failed")
	fmt.Printf("\n")

	fmt.Printf("Retrieving product ID %s...", productID)
	getCall := products.Get(merchantID, productID)
	productInfo, err = getCall.Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.\n")
	fmt.Printf("Retrieved product %s with title %s\n",
		productInfo.Id, productInfo.Title)

	fmt.Printf("Deleting product ID %s...", productID)
	deleteCall := products.Delete(merchantID, productID)
	err = deleteCall.Do()
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
