package main

// This file contains all the OAuth2-related functions for this sample.
// The only function used in other files is authWithGoogle().

import (
	"encoding/gob"
	"fmt"
	"hash/fnv"
	"io/ioutil"
	"log"
	"net/http"
	"net/http/httptest"
	"net/url"
	"os"
	"path"
	"path/filepath"
	"runtime"
	"strings"
	"time"

	"github.com/pkg/browser"
	"golang.org/x/net/context"
	"golang.org/x/oauth2"
	"golang.org/x/oauth2/google"
	"google.golang.org/api/content/v2"
)

func authWithGoogle(ctx context.Context) *http.Client {
	cwd, err := os.Getwd()
	check(err)
	serviceAccountPath := path.Join(cwd, "content-service.json")
	oauth2ClientPath := path.Join(cwd, "content-oauth2.json")

	// First check for service account info, since it's the easier auth
	// flow. Fall back to OAuth2 client if it's not there.
	if _, err = os.Stat(serviceAccountPath); err == nil {
		json, err := ioutil.ReadFile(serviceAccountPath)
		check(err)
		config, err := google.JWTConfigFromJSON(json, content.ContentScope)
		check(err)
		fmt.Printf("Service account credentials for user %s found.\n", config.Email)
		return config.Client(ctx)
	}
	if _, err := os.Stat(oauth2ClientPath); err == nil {
		json, err := ioutil.ReadFile(oauth2ClientPath)
		check(err)
		config, err := google.ConfigFromJSON(json, content.ContentScope)
		check(err)
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

func osUserCacheDir() string {
	switch runtime.GOOS {
	case "darwin":
		return filepath.Join(os.Getenv("HOME"), "Library", "Caches")
	case "linux", "freebsd":
		return filepath.Join(os.Getenv("HOME"), ".cache")
	}
	log.Printf("TODO: osUserCacheDir on GOOS %q", runtime.GOOS)
	return "."
}

func tokenCacheFile(config *oauth2.Config) string {
	hash := fnv.New32a()
	hash.Write([]byte(config.ClientID))
	hash.Write([]byte(config.ClientSecret))
	hash.Write([]byte(strings.Join(config.Scopes, " ")))
	fn := fmt.Sprintf("go-api-demo-tok%v", hash.Sum32())
	return filepath.Join(osUserCacheDir(), url.QueryEscape(fn))
}

// tokenFromFile tries to read a (cached) OAuth2 refresh token from the given
// filename. If this fails, then it returns an error, which allows us to fall
// back to web authentication if the refresh token isn't already cached.
func tokenFromFile(file string) (*oauth2.Token, error) {
	f, err := os.Open(file)
	if err != nil {
		return nil, err
	}
	t := new(oauth2.Token)
	err = gob.NewDecoder(f).Decode(t)
	return t, err
}

func saveToken(file string, token *oauth2.Token) {
	f, err := os.Create(file)
	if err != nil {
		log.Printf("Warning: failed to cache oauth token: %v", err)
		return
	}
	defer f.Close()
	gob.NewEncoder(f).Encode(token)
}

func newOAuthClient(ctx context.Context, config *oauth2.Config) *http.Client {
	cacheFile := tokenCacheFile(config)
	token, err := tokenFromFile(cacheFile)
	if err != nil {
		token = tokenFromWeb(ctx, config)
		saveToken(cacheFile, token)
	}

	return config.Client(ctx, token)
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

func valueOrFileContents(value string, filename string) string {
	if value != "" {
		return value
	}
	slurp, err := ioutil.ReadFile(filename)
	if err != nil {
		log.Fatalf("Error reading %q: %v", filename, err)
	}
	return strings.TrimSpace(string(slurp))
}
