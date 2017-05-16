/*
This is a set of simple samples written in Go, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

Prerequisites

Please make sure that you have Go installed and that you've installed
the Google APIs Client Library for Go as well as the OAuth Library
for Go and its support for interfacing with Google APIs:

        $ go get golang.org/x/oauth2
        $ go get golang.org/x/oauth2/google
        $ go get google.golang.org/api/content/v2

This code also uses the browser package to open the authentication URL in
your browser automatically and the support package for Google's protocol
buffers:

        $ go get github.com/pkg/browser
        $ go get github.com/golang/protobuf/proto

Finally, this code uses the backoff package to handle retries:

        $ go get github.com/cenkalti/backoff


Setup Authentication and Sample Configuration

If you have not already, please read the top-level README in the
GitHub repository to discover how to set up both authentication and
the common sample configuration.  The rest of this document assumes
you have performed both tasks.

The GitHub repository is at:
https://github.com/googleads/googleads-shopping-samples

Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.

1. Compile all the sample code in the directory together:

        $ go build -o content-api-demo *.go

2. Run the resulting binary to get an idea of its usage:

        $ ./content-api-demo -h

3. Pick a demo (or demos) to run, for example:

        $ ./content-api-demo products inventory

   If using an OAuth2 client ID for the first time, the application will open a
   browser automatically so you can agree to the OAuth2 access.  The access
   token will be stored in your sample configuration file, so if you
   have authentication issues, delete the token field and reauthenticate.

   If no demos are selected on the command line, then all demos except for the
   Orders demo will be run.

4. Examine your shell output, be inspired and start hacking an amazing new app!
*/
package main
