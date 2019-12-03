package main

// This file contains a demo of using the Accountstatuses service by
// retrieving the account status for the requested merchant ID and
// printing out any data quality issues (if any), along with which
// products are impacted.

import (
	"fmt"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2.1"
)

func accountstatusesDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	accountstatuses := content.NewAccountstatusesService(service)

	fmt.Printf("Getting account status:\n")
	accountStatus, err := accountstatuses.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Getting account status failed")
	}
	printAccountStatus(accountStatus)

	if !config.IsMCA {
		return
	}

	fmt.Printf("Printing statuses of subaccounts of %d:\n", config.MerchantID)
	if err := accountstatuses.List(config.MerchantID).Pages(ctx, printAccountStatusesPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccount statuses failed")
	}
	fmt.Println("")
}

func printAccountStatusesPage(res *content.AccountstatusesListResponse) error {
	for _, as := range res.Resources {
		printAccountStatus(as)
	}
	return nil
}

func printAccountStatus(accountStatus *content.AccountStatus) {
	fmt.Printf(" - Account %s\n", accountStatus.AccountId)

	issueCount := 0

	for _, accountStatusProduct := range accountStatus.Products {
		for _, itemLevelIssue := range accountStatusProduct.ItemLevelIssues {
			issueCount++
			fmt.Printf("  - Issue: [%s] '%s' affecting %d items\n",
				itemLevelIssue.Code,
				itemLevelIssue.Detail,
				itemLevelIssue.NumItems)
		}
	}

	fmt.Printf("Total number of data quality issues: %d\n", issueCount)
}
