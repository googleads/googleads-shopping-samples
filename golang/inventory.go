package main

// This file contains a demo of using the Inventory service by creating a
// sample product with a random offerId (uses code from product.go),
// inserting it, changing the price and inventory, and then retrieving
// it to check the changed price.  Afterwards, the new product is deleted.

import (
	"fmt"
	"math/rand"

	content "google.golang.org/api/content/v2"
)

func inventoryDemo(contentService *content.APIService, merchantID uint64) {
	offerID := fmt.Sprintf("book#test%d", rand.Int())
	product := createSampleProduct(offerID)

	newPrice := content.Price{
		Currency: "US",
		Value:    "5.00",
	}
	invReq := content.InventorySetRequest{
		Price:        &newPrice,
		Availability: "out of stock",
	}

	products := content.NewProductsService(contentService)
	inventory := content.NewInventoryService(contentService)

	fmt.Printf("Inserting product with offerId %s... ", offerID)
	insertCall := products.Insert(merchantID, product)
	productInfo, err := insertCall.Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	productID := productInfo.Id

	fmt.Printf("Retrieving product ID %s...", productID)
	getCall := products.Get(merchantID, productID)
	productInfo, err = getCall.Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.")
	fmt.Printf("Retrieved product %s @ (%s, %s)\n\n",
		productInfo.Id, productInfo.Availability,
		productInfo.Price.Currency)

	fmt.Printf("Setting new price and availability...")
	invCall := inventory.Set(merchantID, product.Channel,
		productID, &invReq)
	_, err = invCall.Do()
	checkAPI(err, "Inventory set failed")
	fmt.Printf("done.\n\n")

	fmt.Printf("Retrieving product ID %s...", productID)
	getCall = products.Get(merchantID, productID)
	productInfo, err = getCall.Do()
	checkAPI(err, "Retrieval failed")
	fmt.Printf("done.\n")
	fmt.Printf("Retrieved product %s @ (%s, %s)\n\n",
		productInfo.Id, productInfo.Availability,
		productInfo.Price.Currency)

	fmt.Printf("Deleting product ID %s...", productID)
	deleteCall := products.Delete(merchantID, productID)
	err = deleteCall.Do()
	checkAPI(err, "Deletion failed")
	fmt.Printf("done.\n")
}
