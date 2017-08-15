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

const endpointEnvVar = "GOOGLE_SHOPPING_SAMPLES_ENDPOINT"

var demos = map[string](func(context.Context, *content.APIService, *merchantInfo)){
	"products":         productDemo,
	"datafeeds":        datafeedDemo,
	"inventory":        inventoryDemo,
	"productsBatch":    productsBatchDemo,
	"accountstatuses":  accountstatusesDemo,
	"productstatuses":  productstatusesDemo,
	"accounts":         accountDemo,
	"shippingSettings": shippingSettingsDemo,
	"accountTax":       accountTaxDemo,
	"orders":           ordersDemo,
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
	noConfig := flag.Bool("noconfig", false, "run samples with no configuration directory")
	logFile := flag.String("log_file", "", "filename for logging API requests and responses")

	flag.Usage = usage
	flag.Parse()
	samplesConfig := merchantInfo{}
	if !*noConfig {
		if _, err := os.Stat(*configPath); os.IsNotExist(err) {
			log.Fatalf("Configuration directory %s does not exist", *configPath)
		}
		samplesConfig.Path = path.Join(*configPath, "content")
		if _, err := os.Stat(samplesConfig.Path); os.IsNotExist(err) {
			log.Fatalf("Content API configuration directory %s does not exist", samplesConfig.Path)
		}
		samplesConfig.read()
	}

	// Set up random seed so we get different offer IDs
	rand.Seed(time.Now().Unix())
	// Set up the API service to be passed to the demos.
	ctx := context.Background()
	client := authWithGoogle(ctx, samplesConfig)
	if *logFile != "" {
		f, err := os.OpenFile(*logFile, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
		if err != nil {
			log.Fatalf("Failed to open log file: %s", err.Error())
		}
		defer func() {
			if err := f.Close(); err != nil {
				log.Fatalf("Failed to close log file: %s", err.Error())
			}
		}()
		logClient(client, f)
	}
	contentService, err := content.New(client)
	if err != nil {
		log.Fatal(err)
	}
	contentService.UserAgent = "Content API for Shopping Samples"
	baseURL := os.Getenv(endpointEnvVar)
	if baseURL != "" {
		// There may be other issues with the base URL that show up during calls,
		// but let's do some straightforward syntactic checks here.
		u, err := url.Parse(baseURL)
		if err != nil {
			log.Fatal("Failure to parse " + endpointEnvVar + " value as URL: " + err.Error())
		}
		if !u.IsAbs() {
			log.Fatal("Expected absolute URL for " + endpointEnvVar + " value: " + baseURL)
		}
		// The API client expects the contents of BasePath will have a trailing /.
		contentService.BasePath = strings.TrimSuffix(u.String(), "/") + "/"
		fmt.Println("Using non-standard API endpoint URL: " + contentService.BasePath)
	}
	samplesConfig.retrieve(ctx, contentService)

	modules := flag.Args()
	// If no modules were specified, then run all non-Orders demos.
	if len(modules) == 0 {
		for k := range demos {
			if k != "orders" {
				modules = append(modules, k)
			}
		}
	}

	for _, d := range modules {
		demo, ok := demos[d]
		if !ok {
			fmt.Fprintf(os.Stderr, "Invalid service: %s\n\n", d)
			printDemos(os.Stderr)
			os.Exit(1)
		}
		fmt.Printf("Running demo %s...\n", d)
		demo(ctx, contentService, &samplesConfig)
		fmt.Printf("Finished running demo %s.\n", d)
		fmt.Println("-------------------------")
	}
}
