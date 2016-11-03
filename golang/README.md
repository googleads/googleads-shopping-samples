# Google Content API for Shopping Go Samples

This is a set of simple samples written in Go, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you have Go installed and that you've installed
the [Google APIs Client Library for Go]
(https://github.com/google/google-api-go-client) as well as the [OAuth Library
for Go](https://github.com/golang/oauth2) and its support for
interfacing with Google APIs:

        $ go get golang.org/x/oauth2
        $ go get golang.org/x/oauth2/google
        $ go get google.golang.org/api/content/v2

This code also uses the [browser](https://github.com/pkg/browser) package
to open the authentication URL in your browser automatically:

        $ go get github.com/pkg/browser

## Setup Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
instead to simplify the authentication flow. These samples also support using
[Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.  Also make sure the
files are in your [GOPATH](https://github.com/golang/go/wiki/GOPATH).

The Go samples share the location and format of configuration info with many of
the other samples. You will need to create a directory called
`.shopping-content-samples` in your home directory. You will also need a basic
configuration file that includes your Merchant Center ID. The file
`merchant-info.json` has a barebones configuration you can copy and adjust.

1. Set up your desired authentication method.

   If you are using an OAuth2 client ID:

   * Download your [OAuth2 client credentials](console.developers.google.com/apis/credentials)
     to `content-oauth2.json` in the configuration directory mentioned above.

   If you are using a service account:

    * Put the JSON file you downloaded when creating the service account with
      the filename `content-service.json` in the configuration directory
      mentioned above.

2. Compile all the sample code together directory:

        $ go build -o content-api-demo *.go

3. Run the resulting binary to get an idea of its usage:

        $ ./content-api-demo -h

4. Pick a demo (or demos) to run, for example:

        $ ./content-api-demo products inventory

   If using an OAuth2 client ID for the first time, the application will open a
   browser automatically so you can agree to the OAuth2 access.  The access
   token will be stored in your `merchant-info.json` configuration, so if you
   have authentication issues, delete the `token` field and reauthenticate.

5. Examine your shell output, be inspired and start hacking an amazing new app!
