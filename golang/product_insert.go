package main

// This file contains a demo of using the Product service by creating a
// sample product with a random offerId and inserting it

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2.1"
)

func productsInsertDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
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
	fmt.Printf("Product with ID %s was inserted successfully.\n", productInfo.Id)
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
		Gtin:                  "9780007350896",
		Price:                 &productPrice,
		Shipping:              [](*content.ProductShipping){&shippingInfo},
		ShippingWeight:        &shippingWeight,
	}
	return &product
}
