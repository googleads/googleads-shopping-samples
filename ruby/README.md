# Google Content API for Shopping Ruby Samples

This is a set of simple samples written in Ruby, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you're running Ruby 2.0+ and you've installed the [Google
APIs Client Library for Ruby](https://developers.google.com/api-client-library/ruby/start/installation).

## Setup Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
instead to simplify the authentication flow. These samples also support using
[Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.

1. Create the directory `.shopping-content-samples` inside your home
   directory.  The configuration and authentication credentials for the
   samples will be stored there.

   Also copy the `merchant-info.json` file to that new directory and edit
   it to include your merchant ID (`merchantId`) and Google login
   (`emailAddress`).  If the merchant ID belongs to a multi-client
   account, set `isMCA` to `true`.

2. Set up your desired authentication method.

   If you are using an OAuth2 client ID:

   * Download your [OAuth2 client credentials](https://console.developers.google.com/apis/credentials)
     to `content-oauth2.json` in the configuration directory described above.

   If you are using a service account:

    * Put the JSON file you downloaded when creating the service account in
      with the filename `content-service.json` in the configuration directory
      described above.

3. Start up a sample:

        $ ruby product/list_products.rb

   If using an OAuth2 client ID for the first time, you will need to access
   the authorization URL printed on the terminal in your browser and paste
   the resulting refresh token into the terminal.

4. Examine your shell output, be inspired and start hacking an amazing new app!
