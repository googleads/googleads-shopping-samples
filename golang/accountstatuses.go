package main

// This file contains a demo of using the Accountstatuses service by
// retrieving the account status for the requested merchant ID and
// printing out any data quality issues (if any), along with which
// products are impacted.

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

func accountstatusesDemo(ctx context.Context, service *content.APIService, merchantID uint64) {
	accountstatuses := content.NewAccountstatusesService(service)

	fmt.Printf("Getting account status:\n")
	accountStatus, err := accountstatuses.Get(merchantID, merchantID).Do()
	checkAPI(err, "Getting account status failed")
	printAccountStatus(accountStatus)
}

func printAccountStatus(accountStatus *content.AccountStatus) {
	fmt.Printf(" - Account %s\n", accountStatus.AccountId)
	for _, dataQualityIssue := range accountStatus.DataQualityIssues {
		fmt.Printf("\t(%s) %s: %s\n",
			dataQualityIssue.Severity, dataQualityIssue.Id,
			dataQualityIssue.SubmittedValue)
		for _, exampleItem := range dataQualityIssue.ExampleItems {
			fmt.Printf("\t - Item %s: %s\n",
				exampleItem.ItemId, exampleItem.Title)
		}
	}
	fmt.Printf("\n")
}
