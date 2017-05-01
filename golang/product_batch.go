package main

// This file contains a demo of using the Product service with batch
// calls by creating a few sample product with a random offerId,
// inserting them in one batch, listing the available products,
// and then removing them with another batch call.

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func productsBatchDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	productsToSend := [](*content.Product){
		createSampleProduct(config, fmt.Sprintf("book#test%d", rand.Int())),
		createSampleProduct(config, fmt.Sprintf("book#test%d", rand.Int())),
		createSampleProduct(config, fmt.Sprintf("book#test%d", rand.Int())),
		createSampleProduct(config, fmt.Sprintf("book#test%d", rand.Int())),
	}

	products := content.NewProductsService(service)

	fmt.Printf("Inserting %d products... ", len(productsToSend))
	var insertReqs = make([](*content.ProductsCustomBatchRequestEntry),
		len(productsToSend))
	for n, prod := range productsToSend {
		entry := content.ProductsCustomBatchRequestEntry{
			BatchId:    int64(n + 1),
			MerchantId: config.MerchantID,
			Product:    prod,
			Method:     "insert",
		}
		insertReqs[n] = &entry
	}
	insertBatch := content.ProductsCustomBatchRequest{
		Entries: insertReqs,
	}
	insertCall := products.Custombatch(&insertBatch)
	responses, err := insertCall.Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Batch call for insertion failed")
	}
	fmt.Printf("done.\n")
	var productIDs = make([]string, len(productsToSend))
	for n, resp := range responses.Entries {
		if resp.Errors != nil {
			fmt.Printf("Item %d in batch failed.\n",
				resp.BatchId)
			checkContentErrors(resp.Errors.Errors, true)
		} else {
			fmt.Printf("Item %d in batch succeeded.\n",
				resp.BatchId)
			checkContentErrors(resp.Product.Warnings, false)
			productIDs[n] = resp.Product.Id
		}
	}
	fmt.Printf("\n")

	fmt.Printf("Listing products:\n")
	listCall := products.List(config.MerchantID)
	if err := listCall.Pages(ctx, printProductsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing products failed")
	}
	fmt.Printf("\n")

	fmt.Printf("Deleting %d products... ", len(productsToSend))
	var deleteReqs = make([](*content.ProductsCustomBatchRequestEntry),
		len(productsToSend))
	for n, prodID := range productIDs {
		entry := content.ProductsCustomBatchRequestEntry{
			BatchId:    int64(n + 1),
			MerchantId: config.MerchantID,
			ProductId:  prodID,
			Method:     "delete",
		}
		deleteReqs[n] = &entry
	}
	deleteBatch := content.ProductsCustomBatchRequest{
		Entries: deleteReqs,
	}
	deleteCall := products.Custombatch(&deleteBatch)
	responses, err = deleteCall.Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Batch call for deletion failed")
	}
	fmt.Printf("done.\n")
	for _, resp := range responses.Entries {
		if resp.Errors != nil {
			fmt.Printf("Item %d in batch failed.\n",
				resp.BatchId)
			checkContentErrors(resp.Errors.Errors, true)
		} else {
			fmt.Printf("Item %d in batch succeeded.\n",
				resp.BatchId)
		}
	}
}
