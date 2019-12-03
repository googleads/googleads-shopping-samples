package main

// This file contains a demo of using the Accounts service that
// has two parts:
// * Getting information about the current MC account as well as
//   adding/removing users and Google Ads links, which can
//   be done by both multi-client and non-multi-client accounts.
// * Adding, listing, and removing subaccounts, which is only
//   valid for an MCA account.

import (
	"fmt"
	"math/rand"
	"time"

	"github.com/cenkalti/backoff"
	"golang.org/x/net/context"
	"google.golang.org/api/content/v2.1"
)

func accountDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	primaryAccountDemo(service, config)
	if config.IsMCA {
		multiClientAccountDemo(ctx, service, config)
	}
}

// primaryAccountDemo gets the account information for the currently
// configured Merchant Center account, and then adds/removes a new
// user and/or new Google Ads account link, if those fields in the
// configuration are not the default value.
func primaryAccountDemo(service *content.APIService, config *merchantInfo) {
	changed := false
	accounts := content.NewAccountsService(service)

	fmt.Println("Getting account information.")
	account, err := accounts.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Getting account information failed")
	}
	printAccount(account)

	if config.AccountSampleUser != "" {
		fmt.Printf("Adding user %s.\n", config.AccountSampleUser)
		account.Users = append(account.Users, &content.AccountUser{
			Admin:        false,
			EmailAddress: config.AccountSampleUser,
		})
		changed = true
	}

	if config.AccountSampleGoogleAdsID != 0 {
		fmt.Printf("Linking Google Ads ID %d.\n", config.AccountSampleGoogleAdsID)
		account.AdsLinks = append(account.AdsLinks, &content.AccountAdsLink{
			AdsId:  config.AccountSampleGoogleAdsID,
			Status: "active",
		})
		changed = true
	}

	if !changed {
		fmt.Println("No account changes available in sample configuration.")
		return
	}

	fmt.Println("Updating account information.")
	account, err = accounts.Update(config.MerchantID, config.MerchantID, account).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating account information failed")
	}
	printAccount(account)

	fmt.Println("Rolling back changes.")

	account, err = accounts.Get(config.MerchantID, config.MerchantID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Getting account information failed")
	}

	if config.AccountSampleUser != "" {
		users := [](*content.AccountUser){}
		fmt.Printf("Removing user %s.\n", config.AccountSampleUser)
		for _, user := range account.Users {
			if user.EmailAddress != config.AccountSampleUser {
				users = append(users, user)
			}
		}
		account.Users = users
	}

	if config.AccountSampleGoogleAdsID != 0 {
		links := []*content.AccountAdsLink{}
		fmt.Printf("Removing link to Google Ads ID %d.\n", config.AccountSampleGoogleAdsID)
		for _, link := range account.AdsLinks {
			if link.AdsId != config.AccountSampleGoogleAdsID {
				links = append(links, link)
			}
		}
		account.AdsLinks = links
	}

	fmt.Println("Reverting account information.")
	account, err = accounts.Update(config.MerchantID, config.MerchantID, account).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Reverting account information failed")
	}
	printAccount(account)
}

// multiClientAccountDemo retrieves the list of subaccounts from the
// configured multi-client account and adds and subsequently removes
// a new subaccount, relisting the subaccounts after each action to
// show the changes.  This demo cannot be run on a non-multi-client account.
func multiClientAccountDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if !config.IsMCA {
		fmt.Println("This demo requires a multi-client account.")
		return
	}
	accounts := content.NewAccountsService(service)

	fmt.Printf("Printing subaccounts of %d:\n", config.MerchantID)
	listCall := accounts.List(config.MerchantID)
	// Enable this to change the number of results listed by
	// per page:
	if false {
		listCall.MaxResults(100)
	}
	err := listCall.Pages(ctx, printAccountsPage)
	if err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccounts failed")
	}
	fmt.Println("")

	accountName := fmt.Sprintf("sampleAccount#%d", rand.Int())
	account := &content.Account{
		Name: accountName,
	}
	fmt.Printf("Adding subaccount with name %s.\n", accountName)
	account, err = accounts.Insert(config.MerchantID, account).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Adding subaccount failed")
	}
	fmt.Printf("Subaccount added with ID %d.\n", account.Id)

	fmt.Printf("Retrieving subaccount with ID %d.\n", account.Id)
	// Since accounts may not be immediately available after creation, perform
	// a simplistic back-off algorithm to retry a few times.
	operation := func() error {
		account, err := accounts.Get(config.MerchantID, account.Id).Do()
		if err != nil {
			return err
		}
		printAccount(account)
		fmt.Println("")
		return nil
	}
	notify := func(err error, d time.Duration) {
		fmt.Printf("Failed to retrieve subaccount, will retry after %s.\n", d)
	}
	backoffSettings := backoff.NewExponentialBackOff()
	backoffSettings.InitialInterval = 5 * time.Second
	backoffSettings.MaxInterval = 30 * time.Second
	backoffSettings.MaxElapsedTime = 60 * time.Second
	if err := backoff.RetryNotify(operation, backoffSettings, notify); err != nil {
		dumpAPIErrorAndStop(err, "Retrieving new subaccount failed")
	}

	fmt.Printf("Printing subaccounts of %d:\n", config.MerchantID)
	if err := accounts.List(config.MerchantID).Pages(ctx, printAccountsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccounts failed")
	}
	fmt.Println("")

	fmt.Printf("Removing subaccount with ID %d.\n", account.Id)
	if err := accounts.Delete(config.MerchantID, account.Id).Do(); err != nil {
		dumpAPIErrorAndStop(err, "Removing subaccount failed")
	}
	fmt.Println("Subaccount removed.")

	fmt.Printf("Printing subaccounts of %d:\n", config.MerchantID)
	if err := accounts.List(config.MerchantID).Pages(ctx, printAccountsPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing subaccounts failed")
	}
	fmt.Println("")
}

func printAccountsPage(res *content.AccountsListResponse) error {
	for _, account := range res.Resources {
		printAccount(account)
	}
	return nil
}

func printAccount(account *content.Account) {
	fmt.Printf("Information for account %d:\n", account.Id)
	fmt.Printf("- Display name: %s\n", account.Name)
	if len(account.Users) == 0 {
		fmt.Println("- No registered users.")
	} else {
		fmt.Println("- Registered users:")
		for _, user := range account.Users {
			fmt.Print("  - ")
			if user.Admin {
				fmt.Print("(ADMIN) ")
			}
			fmt.Println(user.EmailAddress)
		}
	}
	if len(account.AdsLinks) == 0 {
		fmt.Println("- No linked Google Ads accounts.")
	} else {
		fmt.Println("- Linked Google Ads accounts:")
		for _, link := range account.AdsLinks {
			fmt.Printf("  - %d: %s\n", link.AdsId, link.Status)
		}
	}
}
