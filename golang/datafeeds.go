package main

// This file contains a demo of using the Datafeed service by creating a
// sample datafeed with a random filename, inserting it, listing the
// existing feeds and retrieving it (to show that it was indeed inserted),
// and then deleting the new feed.

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func datafeedDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	feedName := fmt.Sprintf("feed%d", rand.Int())

	fmt.Printf("Inserting datafeed with filename %s... ", feedName)
	datafeeds := content.NewDatafeedsService(service)
	feed := createSampleDatafeed(feedName)

	insertedFeed, err := datafeeds.Insert(config.MerchantID, feed).Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	feedID := insertedFeed.Id
	fmt.Printf("New feed ID: %d\n\n", feedID)

	fmt.Printf("Listing datafeeds:\n")
	listCall := datafeeds.List(config.MerchantID)
	// Enable this to change the number of results listed by
	// per page:
	if false {
		listCall.MaxResults(100)
	}
	err = listCall.Pages(ctx, printFeedsPage)
	checkAPI(err, "Listing feeds failed")
	fmt.Printf("\n")

	fmt.Printf("Retrieving datafeed %d... ", feedID)
	_, err = datafeeds.Get(config.MerchantID, uint64(feedID)).Do()
	checkAPI(err, "Retrieving feed failed")
	fmt.Printf("done.\n")

	fmt.Printf("Removing datafeed %d... ", feedID)
	err = datafeeds.Delete(config.MerchantID, uint64(feedID)).Do()
	checkAPI(err, "Removing feed failed")
	fmt.Printf("done.\n\n")
}

func printFeedsPage(res *content.DatafeedsListResponse) error {
	for _, feed := range res.Resources {
		fmt.Printf(" - Id %d, Name %s\n", feed.Id, feed.Name)
	}
	return nil
}

func createSampleDatafeed(name string) *content.Datafeed {
	fetchSchedule := content.DatafeedFetchSchedule{
		Weekday:  "monday",
		Hour:     6,
		TimeZone: "America/Los_Angeles",
		FetchUrl: fmt.Sprintf("https://feeds.myshop.com/%s", name),
	}
	feedFormat := content.DatafeedFormat{
		FileEncoding:    "utf-8",
		ColumnDelimiter: "tab",
		QuotingMode:     "value quoting",
	}
	feed := content.Datafeed{
		Name:                 name,
		ContentType:          "products",
		AttributeLanguage:    "en",
		ContentLanguage:      "en",
		IntendedDestinations: []string{"Shopping"},
		FileName:             name,
		TargetCountry:        "US",
		FetchSchedule:        &fetchSchedule,
		Format:               &feedFormat,
	}
	return &feed
}
