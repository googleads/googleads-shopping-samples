package main

import (
	"fmt"
	"log"
	"os"

	"google.golang.org/api/content/v2"
	"google.golang.org/api/googleapi"
)

// This file just contains common functions used by the others.

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
