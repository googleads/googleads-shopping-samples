package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	"os/user"
	"path"

	"golang.org/x/oauth2"
	"google.golang.org/api/content/v2"
	"google.golang.org/api/googleapi"
)

// This file just contains common functions used by the others.

// Configuration should be stored in .shopping-content-samples in the
// user's home directory.
var configPath = func() string {
	usr, err := user.Current()
	check(err)
	return path.Join(usr.HomeDir, ".shopping-content-samples")
}()

// Information stored in the 'merchant-info.json' file in the config directory.
type merchantInfo struct {
	MerchantID             uint64        `json:"merchantId,omitempty"`
	ApplicationName        string        `json:"applicationName,omitempty"`
	EmailAddress           string        `json:"emailAddress,omitempty"`
	WebsiteURL             string        `json:"websiteUrl,omitempty"`
	AccountSampleUser      string        `json:"accountSampleUser,omitempty"`
	AccountSampleAdwordsID uint64        `json:"accountSampleAdWordsCID,omitempty"`
	IsMCA                  bool          `json:"isMCA,omitempty"`
	Token                  *oauth2.Token `json:"token,omitempty"`
}

var samplesConfig merchantInfo
var samplesConfigFile = path.Join(configPath, "merchant-info.json")

// Read the contents of merchant-info.json.
func readSamplesConfig() {
	jsonBlob, err := ioutil.ReadFile(samplesConfigFile)
	if err != nil {
		check(fmt.Errorf("failed to decode JSON file %s: %v", samplesConfigFile, err))
	}
	err = json.Unmarshal(jsonBlob, &samplesConfig)
	check(err)
}

// Write the config to merchant-info.json. (Mostly used to store refresh token.)
func writeSamplesConfig() {
	jsonBlob, err := json.MarshalIndent(samplesConfig, "", "  ")
	check(err)
	err = ioutil.WriteFile(samplesConfigFile, jsonBlob, 0660)
	check(err)
}

// Simplify error handling for most non-API error cases.
func check(e error) {
	if e != nil {
		log.Fatal(e)
	}
}

// For handling errors from the API:
func checkAPI(e error, prefix string) {
	if e != nil {
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
