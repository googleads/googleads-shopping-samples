package main

import (
	"flag"
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/url"
	"os"
	"os/user"
	"path"
	"strings"
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
	fmt.Fprintf(os.Stderr, "Usage: %s [flags] <demo> ...\n\n", os.Args[0])
	fmt.Fprintf(os.Stderr, "All flags:\n\n")
	flag.PrintDefaults()
	fmt.Fprintf(os.Stderr, "\n")
	printDemos(os.Stderr)
	os.Exit(2)
}

func main() {
	usr, err := user.Current()
	if err != nil {
		log.Fatal(err)
	}
	defaultPath := path.Join(usr.HomeDir, "shopping-samples")
	configPath := flag.String("config_path", defaultPath, "configuration directory for Shopping samples")
	baseURL := flag.String("base_url", "", "base URL for API calls (if non-standard)")

	flag.Usage = usage
	flag.Parse()
	if _, err := os.Stat(*configPath); os.IsNotExist(err) {
		log.Fatalf("Configuration directory %s does not exist", *configPath)
	}

	samplesConfig := merchantInfo{Path: path.Join(*configPath, "content")}
	if _, err := os.Stat(samplesConfig.Path); os.IsNotExist(err) {
		log.Fatalf("Content API configuration directory %s does not exist", samplesConfig.Path)
	}
	samplesConfig.read()

	// Set up random seed so we get different offer IDs
	rand.Seed(time.Now().Unix())
	// Set up the API service to be passed to the demos.
	ctx := context.Background()
	client := authWithGoogle(ctx, samplesConfig)
	contentService, err := content.New(client)
	if err != nil {
		log.Fatal(err)
	}
	if *baseURL != "" {
		// The API client expects the contents of BasePath will have a trailing /.
		s := strings.TrimSuffix(*baseURL, "/") + "/"
		// There may be other issues with the base URL that show up during calls,
		// but let's do some straightforward syntactic checks here.
		u, err := url.Parse(s)
		if err != nil {
			log.Fatal("Failure to parse base_url argument as URL: " + err.Error())
		}
		if !u.IsAbs() {
			log.Fatal("Expected absolute URL for base_url argument: " + *baseURL)
		}
		fmt.Println("Using non-standard API endpoint URL: " + s)
		contentService.BasePath = s
	}
	samplesConfig.IsMCA = checkMCAStatus(ctx, contentService, &samplesConfig)

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
