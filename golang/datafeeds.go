package main

// This file contains a demo of using the Datafeed service by creating a
// sample datafeed with a random filename, inserting it, listing the
// existing feeds and retrieving it (to show that it was indeed inserted),
// and then deleting the new feed.

import (
	"fmt"
	"math/rand"

	"golang.org/x/net/context"
	content "google.golang.org/api/content/v2"
)

func datafeedDemo(contentService *content.APIService, merchantID uint64) {
	feedName := fmt.Sprintf("feed%d", rand.Int())

	fmt.Printf("Inserting datafeed with filename %s... ", feedName)
	datafeeds := content.NewDatafeedsService(contentService)
	feed := createSampleDatafeed(feedName)
	insertCall := datafeeds.Insert(merchantID, feed)
	insertedFeed, err := insertCall.Do()
	checkAPI(err, "Insertion failed")
	fmt.Printf("done.\n")
	feedID := insertedFeed.Id
	fmt.Printf("New feed ID: %d\n\n", feedID)

	fmt.Printf("Listing datafeeds:\n")
	listCall := datafeeds.List(merchantID)
	err = listCall.Pages(context.Background(), printFeedsPage)
	checkAPI(err, "Listing feeds failed")
	fmt.Printf("\n")

	fmt.Printf("Retrieving datafeed %d... ", feedID)
	getCall := datafeeds.Get(merchantID, uint64(feedID))
	_, err = getCall.Do()
	checkAPI(err, "Retrieving feed failed")
	fmt.Printf("done.\n")

	fmt.Printf("Removing datafeed %d... ", feedID)
	deleteCall := datafeeds.Delete(merchantID, uint64(feedID))
	err = deleteCall.Do()
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
