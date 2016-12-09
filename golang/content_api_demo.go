package main

import (
	"flag"
	"fmt"
	"io"
	"log"
	"math/rand"
	"os"
	"time"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

var demos = map[string](func(context.Context, *content.APIService, *merchantInfo)){
	"products":           productDemo,
	"datafeeds":          datafeedDemo,
	"inventory":          inventoryDemo,
	"productsBatch":      productsBatchDemo,
	"accountstatuses":    accountstatusesDemo,
	"productstatuses":    productstatusesDemo,
	"primaryAccount":     primaryAccountDemo,
	"multiClientAccount": multiClientAccountDemo,
	"shippingSettings":   shippingSettingsDemo,
	"accountTax":         accountTaxDemo,
	"orders":             ordersDemo,
}

func printDemos(w io.Writer) {
	fmt.Fprintf(w, "Available demos:\n\n")
	for n := range demos {
		fmt.Fprintf(w, "  * %s\n", n)
	}
	fmt.Fprintf(w, "\n")
}

func usage() {
	fmt.Fprintf(os.Stderr, "Usage: %s <demo> ...\n\n", os.Args[0])
	fmt.Fprintf(os.Stderr, "All flags:\n\n")
	flag.PrintDefaults()
	fmt.Fprintf(os.Stderr, "\n")
	printDemos(os.Stderr)
	os.Exit(2)
}

func main() {
	flag.Usage = usage
	flag.Parse()

	readSamplesConfig()

	// Set up random seed so we get different offer IDs
	rand.Seed(time.Now().Unix())
	// Set up the API service to be passed to the demos.
	ctx := context.Background()
	client := authWithGoogle(ctx)
	contentService, err := content.New(client)
	if err != nil {
		log.Fatal(err)
	}

	for _, d := range flag.Args() {
		demo, ok := demos[d]
		if !ok {
			fmt.Fprintf(os.Stderr, "Invalid service: %s\n\n", d)
			printDemos(os.Stderr)
			os.Exit(1)
		}
		fmt.Printf("Running demo %s...\n", d)
		demo(ctx, contentService, &samplesConfig)
		fmt.Printf("Finished running demo %s.\n", d)
	}
}
