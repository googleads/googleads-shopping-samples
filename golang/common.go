package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"path"

	"golang.org/x/oauth2"
	"google.golang.org/api/content/v2"
	"google.golang.org/api/googleapi"
)

// This file just contains common functions used by the others.

const merchantInfoFilename = "merchant-info.json"

// Information stored for merchants for use by the Shopping samples
type merchantInfo struct {
	MerchantID             uint64        `json:"merchantId,omitempty"`
	ApplicationName        string        `json:"applicationName,omitempty"`
	EmailAddress           string        `json:"emailAddress,omitempty"`
	WebsiteURL             string        `json:"websiteUrl,omitempty"`
	AccountSampleUser      string        `json:"accountSampleUser,omitempty"`
	AccountSampleAdwordsID uint64        `json:"accountSampleAdWordsCID,omitempty"`
	IsMCA                  bool          `json:"isMCA,omitempty"`
	Token                  *oauth2.Token `json:"token,omitempty"`
	Path                   string        `json:"-"`
}

// Reads the contents of merchant-info.json and replaces the current values of JSON-exported fields.
// This function assumes that the Path value has been appropriately set before calling.
func (samplesConfig *merchantInfo) read() {
	samplesConfigFile := path.Join(samplesConfig.Path, merchantInfoFilename)
	jsonBlob, err := ioutil.ReadFile(samplesConfigFile)
	if err != nil {
		log.Fatalf("failed to decode JSON file %s: %v", samplesConfigFile, err)
	}
	if err := json.Unmarshal(jsonBlob, &samplesConfig); err != nil {
		log.Fatal(err)
	}
}

// Write the config to merchant-info.json. (Mostly used to store refresh token.)
// This function assumes that the Path value has been appropriately set before calling.
func (samplesConfig *merchantInfo) write() {
	jsonBlob, err := json.MarshalIndent(samplesConfig, "", "  ")
	if err != nil {
		log.Fatal(err)
	}
	if err := ioutil.WriteFile(path.Join(samplesConfig.Path, merchantInfoFilename), jsonBlob, 0660); err != nil {
		log.Fatal(err)
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
