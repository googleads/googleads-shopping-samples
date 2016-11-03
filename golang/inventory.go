package main

// This file contains a demo of using the Inventory service by creating a
// sample product with a random offerId (uses code from product.go),
// inserting it, changing the price and inventory, and then retrieving
// it to check the changed price.  Afterwards, the new product is deleted.

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func inventoryDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	offerID := fmt.Sprintf("book#test%d", rand.Int())
	product := createSampleProduct(config, offerID)

	newPrice := content.Price{
		Currency: "US",
		Value:    "5.00",
	}
	invReq := content.InventorySetRequest{
		Price:        &newPrice,
		Availability: "out of stock",
	}

	products := content.NewProductsService(service)
	inventory := content.NewInventoryService(service)

	fmt.Printf("Inserting product with offerId %s... ", offerID)
	productInfo, err := products.Insert(config.MerchantID, product).Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	productID := productInfo.Id

	fmt.Printf("Retrieving product ID %s...", productID)
	productInfo, err = products.Get(config.MerchantID, productID).Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.")
	fmt.Printf("Retrieved product %s @ (%s, %s)\n\n",
		productInfo.Id, productInfo.Availability,
		productInfo.Price.Currency)

	fmt.Printf("Setting new price and availability...")
	_, err = inventory.Set(config.MerchantID, product.Channel, productID, &invReq).Do()
	checkAPI(err, "Inventory set failed")
	fmt.Printf("done.\n\n")

	fmt.Printf("Retrieving product ID %s...", productID)
	productInfo, err = products.Get(config.MerchantID, productID).Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.\n")
	fmt.Printf("Retrieved product %s @ (%s, %s)\n\n",
		productInfo.Id, productInfo.Availability,
		productInfo.Price.Currency)

	fmt.Printf("Deleting product ID %s...", productID)
	err = products.Delete(config.MerchantID, productID).Do()
	checkAPI(err, "Deletion failed")
	fmt.Printf("done.\n")
}
