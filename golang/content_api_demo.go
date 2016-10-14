package main

import (
	"flag"
	"fmt"
	"io"
	"math/rand"
	"os"
	"strconv"
	"time"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

var demos = map[string](func(context.Context, *content.APIService, uint64)){
	"products":        productDemo,
	"datafeeds":       datafeedDemo,
	"inventory":       inventoryDemo,
	"productsBatch":   productsBatchDemo,
	"accountstatuses": accountstatusesDemo,
	"productstatuses": productstatusesDemo,
}

func printDemos(w io.Writer) {
	fmt.Fprintf(w, "Available demos:\n\n")
	for n := range demos {
		fmt.Fprintf(w, "  * %s\n", n)
	}
	fmt.Fprintf(w, "\n")
}

func usage() {
	fmt.Fprintf(os.Stderr, "Usage: %s <merchant ID> <demo> ...\n\n", os.Args[0])
	fmt.Fprintf(os.Stderr, "All flags:\n\n")
	flag.PrintDefaults()
	fmt.Fprintf(os.Stderr, "\n")
	printDemos(os.Stderr)
	os.Exit(2)
}

func main() {
	flag.Usage = usage
	flag.Parse()
	if flag.NArg() == 0 {
		usage()
	}

	// Set up random seed so we get different offer IDs
	rand.Seed(time.Now().Unix())
	// Set up the API service to be passed to the demos.
	ctx := context.Background()
	client := authWithGoogle(ctx)
	contentService, err := content.New(client)
	check(err)

	merchantID, err := strconv.ParseUint(flag.Arg(0), 10, 64)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Invalid merchant ID: %s\n", flag.Arg(0))
		usage()
	}

	for _, d := range flag.Args()[1:] {
		demo, ok := demos[d]
		if !ok {
			fmt.Fprintf(os.Stderr, "Invalid service: %s\n\n", d)
			printDemos(os.Stderr)
			os.Exit(1)
		}
		fmt.Printf("Running demo %s...\n", d)
		demo(ctx, contentService, merchantID)
		fmt.Printf("Finished running demo %s.\n", d)
	}
}
