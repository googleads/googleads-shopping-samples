/*
This is a set of simple samples written in Go, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

Prerequisites

Please make sure that you have Go installed and that you've installed
the Google APIs Client Library for Go as well as the OAuth Library
for Go and its support for interfacing with Google APIs:

        $ go get golang.org/x/oauth2
        $ go get golang.org/x/oauth2/google
        $ go get google.golang.org/api/content/v2

This code also uses the github/pkg/browser package
to open the authentication URL in your browser automatically:

        $ go get github.com/pkg/browser

Setup Authentication

Before getting started, check the Getting Started section on the Go client
library documentation page:
https://github.com/google/google-api-go-client/blob/master/GettingStarted.md

Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.  Also make sure the
files are in your GOPATH.

1. Download your OAuth 2.0 client ID
   from https://console.developers.google.com/apis/credentials
   to `content-oauth2.json` in the root of the code directory.

2. Compile all the sample code together in that directory:

        $ go build -o content-api-demo *.go

3. Run the resulting binary to get an idea of its usage:

        $ ./content-api-demo

4. Pick a demo (or demos) to run, for example:

        $ ./content-api-demo <merchant ID> products inventory

5. Complete the authorization steps. The application will automatically open a
Chrome window for you to approve the OAuth2 request.

6. Examine your shell output, be inspired and start hacking an amazing new app!
*/
package main

import (
	"flag"
	"fmt"
	"io"
	"math/rand"
	"os"
	"strconv"
	"time"

	content "google.golang.org/api/content/v2"
)

var demos = map[string](func(*content.APIService, uint64)){
	"products":      productDemo,
	"datafeeds":     datafeedDemo,
	"inventory":     inventoryDemo,
	"productsBatch": productsBatchDemo,
}

func printDemos(w io.Writer) {
	fmt.Fprintf(w, "Possible services:\n\n")
	for n := range demos {
		fmt.Fprintf(w, "  * %s\n", n)
	}
	fmt.Fprintf(w, "\n")
}

func usage() {
	fmt.Fprintf(os.Stderr, "Usage: %s <merchant ID> <service> ...\n\n", os.Args[0])
	printDemos(os.Stderr)
	os.Exit(2)
}

func main() {
	flag.Parse()
	if flag.NArg() == 0 {
		usage()
	}

	// Set up random seed so we get different offer IDs
	rand.Seed(time.Now().Unix())
	// Set up the API service to be passed to the demos.
	client := authWithGoogle()
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
		demo(contentService, merchantID)
		fmt.Printf("Finished running demo %s.\n", d)
	}
}
