package main

// This file contains all the OAuth2-related functions for this sample.
// The only function used in other files is authWithGoogle().

import (
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"net/http/httptest"
	"os"
	"path"
	"time"

	"github.com/pkg/browser"
	"golang.org/x/net/context"
	"golang.org/x/oauth2"
	"golang.org/x/oauth2/google"
	"google.golang.org/api/content/v2"
)

func authWithGoogle(ctx context.Context) *http.Client {
	serviceAccountPath := path.Join(configPath, "service-account.json")
	oauth2ClientPath := path.Join(configPath, "client-secrets.json")

	// First, check for the Application Default Credentials.
	if client, err := google.DefaultClient(ctx, content.ContentScope); err == nil {
		fmt.Println("Using Application Default Credentials.")
		return client
	}
	// Second, check for service account info, since it's the easier auth
	// flow. Fall back to OAuth2 client if it's not there.
	if _, err := os.Stat(serviceAccountPath); err == nil {
		fmt.Printf("Loading service account from %s.\n", serviceAccountPath)
		json, err := ioutil.ReadFile(serviceAccountPath)
		if err != nil {
			log.Fatal(err)
		}
		config, err := google.JWTConfigFromJSON(json, content.ContentScope)
		if err != nil {
			log.Fatal(err)
		}
		fmt.Printf("Service account credentials for user %s found.\n", config.Email)
		return config.Client(ctx)
	}
	if _, err := os.Stat(oauth2ClientPath); err == nil {
		fmt.Printf("Loading OAuth2 client from %s.\n", oauth2ClientPath)
		json, err := ioutil.ReadFile(oauth2ClientPath)
		if err != nil {
			log.Fatal(err)
		}
		config, err := google.ConfigFromJSON(json, content.ContentScope)
		if err != nil {
			log.Fatal(err)
		}
		fmt.Printf("OAuth2 client credentials for application %s found.\n", config.ClientID)
		return newOAuthClient(ctx, config)
	}

	fmt.Fprintln(os.Stderr, "No OAuth2 authentication files found. Checked:")
	fmt.Fprintln(os.Stderr, "- ", serviceAccountPath)
	fmt.Fprintln(os.Stderr, "- ", oauth2ClientPath)
	fmt.Fprintln(os.Stderr, "Please read the accompanying documentation.")
	log.Fatalln("Authentication failed")
	return nil
}

func newOAuthClient(ctx context.Context, config *oauth2.Config) *http.Client {
	if samplesConfig.Token == nil {
		samplesConfig.Token = tokenFromWeb(ctx, config)
		writeSamplesConfig()
	}

	return config.Client(ctx, samplesConfig.Token)
}

func tokenFromWeb(ctx context.Context, config *oauth2.Config) *oauth2.Token {
	ch := make(chan string)
	randState := fmt.Sprintf("st%d", time.Now().UnixNano())
	ts := httptest.NewServer(http.HandlerFunc(func(rw http.ResponseWriter, req *http.Request) {
		if req.URL.Path == "/favicon.ico" {
			http.Error(rw, "", 404)
			return
		}
		if req.FormValue("state") != randState {
			log.Printf("State doesn't match: req = %#v", req)
			http.Error(rw, "", 500)
			return
		}
		if code := req.FormValue("code"); code != "" {
			fmt.Fprintf(rw, "<h1>Success</h1>Authorized.")
			rw.(http.Flusher).Flush()
			ch <- code
			return
		}
		log.Printf("no code")
		ch <- ""
		http.Error(rw, "", 500)
	}))
	defer ts.Close()

	config.RedirectURL = ts.URL
	authURL := config.AuthCodeURL(randState)
	go browser.OpenURL(authURL)
	log.Printf("Authorize this app at: %s", authURL)
	code := <-ch
	if code == "" {
		log.Fatalln("Authentication failed.")
	}
	log.Printf("Got code: %s", code)

	token, err := config.Exchange(ctx, code)
	if err != nil {
		log.Fatalf("Token exchange error: %v", err)
	}
	return token
}
