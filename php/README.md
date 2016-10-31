# Google Content API for Shopping PHP Samples

This is a set of simple samples written in PHP, which provide a minimal
example of Google Shopping integration within a command line application.
These samples are written to be run as a command line application, not as a
webpage.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

These samples require the PHP client library, which requires PHP 5.4+.
Please see the following
[installation instructions](https://developers.google.com/api-client-library/php/start/installation).
A [Composer](https://getcomposer.org/) configuration has been included.
If you are not using Composer, you will need to adjust the `require_once`
line in `BaseSample.php` appropriately.

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

1. Create the directory `$(HOME)/.shopping-content-samples` to store the
   needed configuration.  Errors from the samples related to
   this directory not existing or files within not existing will provide the
   full path to the expected directory/files.

2. Set up your desired authentication method.

   If you are using an OAuth2 client ID:

   * Download your [OAuth2 client credentials](https://console.developers.google.com/apis/credentials)
     to `content-oauth2.json` in `$(HOME)/.shopping-content-samples`.

   If you are using a service account:

    * Put the JSON file you downloaded when creating the service account in
      `$(HOME)/.shopping-content-samples` with the filename
      `content-service.json`.

3. Take the example `merchant-info.json` from the project root, copy
   it into `$(HOME)/.shopping-content-samples`, and then change its
   contents to include your Google login, merchant ID and an application
   name.  If you wish to try the primary account samples
   (AdWords linking/User adding), change the null for those fields
   to an appropriate string. Set the `isMCA` field to true if the Merchant
   Center account you are testing with is an multi-client account.

4. Run any of the included samples.

    ```
    $ php ProductsSample.php
    ```

   If using an OAuth2 client ID for the first time, the application will
   provide a URL for authentication. Load it in your browser, accept the
   access, then paste the resulting code back to the application.

5. Examine your shell output, be inspired and start hacking an amazing new app!
