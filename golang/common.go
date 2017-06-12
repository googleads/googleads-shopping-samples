package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"path"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
	"google.golang.org/api/googleapi"
)

// This file just contains common functions used by the others.

const merchantInfoFilename = "merchant-info.json"

// Information stored for merchants for use by the Shopping samples
type merchantInfo struct {
	MerchantID             uint64 `json:"merchantId,omitempty"`
	WebsiteURL             string `json:"-"`
	AccountSampleUser      string `json:"accountSampleUser,omitempty"`
	AccountSampleAdwordsID uint64 `json:"accountSampleAdWordsCID,omitempty"`
	IsMCA                  bool   `json:"-"`
	Path                   string `json:"-"`
}

// Reads the contents of merchant-info.json and replaces the current values of JSON-exported fields.
// This function assumes that the Path value has been appropriately set before calling.
func (samplesConfig *merchantInfo) read() {
	samplesConfigFile := path.Join(samplesConfig.Path, merchantInfoFilename)
	jsonBlob, err := ioutil.ReadFile(samplesConfigFile)
	if err != nil {
		fmt.Printf("Configuration file %s cannot be read.\n", samplesConfigFile)
		fmt.Println("Falling back to configuration based on authenticated user.")
		return
	}
	if err := json.Unmarshal(jsonBlob, &samplesConfig); err != nil {
		log.Fatalf("Failed to decode JSON file %s: %v", samplesConfigFile, err)
	}
}

// Retrieve Merchant Center-located information for the configured merchant.
func (samplesConfig *merchantInfo) retrieve(ctx context.Context, service *content.APIService) {
	accounts := content.NewAccountsService(service)
	fmt.Println("Getting authenticated account information.")
	authinfo, err := accounts.Authinfo().Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Getting information for authenticated account failed")
	}
	if len(authinfo.AccountIdentifiers) == 0 {
		log.Fatal("The current authenticated user has no access to any Merchant Center accounts.")
	}
	// If we have no configured Merchant Center ID, then default to the first one provided
	// from authinfo.
	if samplesConfig.MerchantID == 0 {
		firstAccount := authinfo.AccountIdentifiers[0]
		if firstAccount.MerchantId == 0 {
			samplesConfig.MerchantID = firstAccount.AggregatorId
		} else {
			samplesConfig.MerchantID = firstAccount.MerchantId
		}
		fmt.Printf("Using Merchant Center %d for running samples.\n", samplesConfig.MerchantID)
	}
	// If the configured Merchant Center ID is an MCA, then the authenticated account must
	// have access to it to use it, and so it should show up in the authinfo results.
	samplesConfig.IsMCA = false
CheckAccounts:
	for _, i := range authinfo.AccountIdentifiers {
		switch samplesConfig.MerchantID {
		case i.MerchantId:
			break CheckAccounts
		case i.AggregatorId:
			samplesConfig.IsMCA = true
			break CheckAccounts
		}
	}
	if samplesConfig.IsMCA {
		fmt.Printf("Merchant Center %d is an MCA.\n", samplesConfig.MerchantID)
	} else {
		fmt.Printf("Merchant Center %d is not an MCA.\n", samplesConfig.MerchantID)
	}
	acc, err := accounts.Get(samplesConfig.MerchantID, samplesConfig.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Getting Merchant Center account information failed")
	}
	samplesConfig.WebsiteURL = acc.WebsiteUrl
	fmt.Printf("Website for Merchant Center %d: ", samplesConfig.MerchantID)
	if samplesConfig.WebsiteURL == "" {
		fmt.Println("<none>")
	} else {
		fmt.Println(samplesConfig.WebsiteURL)
	}
}

// For handling errors from the API:
func dumpAPIErrorAndStop(e error, prefix string) {
	gError, ok := e.(*googleapi.Error)
	if ok {
		fmt.Fprintf(os.Stderr, "\n\n%s:\nError %d: %s\n\n",
			prefix, gError.Code, gError.Message)
		log.Fatalln("Error from API, halting demos")
	} else {
		fmt.Fprintf(os.Stderr, "Non-API error (type %T) occurred.\n", e)
		log.Fatal(e)
	}
}

// For handling errors and warnings represented as arrays of content.Error
func checkContentErrors(warnings [](*content.Error), isError bool) {
	if len(warnings) > 0 {
		if isError {
			fmt.Printf("Errors received:\n")
		} else {
			fmt.Printf("Warnings received:\n")
		}
		for _, warning := range warnings {
			fmt.Printf(" - %s\n", warning.Message)
		}
		fmt.Printf("\n")
	}
}
