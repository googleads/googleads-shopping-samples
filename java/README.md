# Google Content API for Shopping Java Samples

This is a set of simple samples written in Java, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you're running Java 5+. If you use Maven, you can use
the included `pom.xml` to install the required dependencies.  Otherwise,
install the
[Content API for Shopping Client Library for Java](https://developers.google.com/api-client-library/java/apis/content/v2).

## Setup Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
instead to simplify the authentication flow.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory and set up the
project appropriately for access to the Google APIs Client Library for Java.

1. Create the directory `$(HOME)/.shopping-content-samples` to store the
   needed configuration.

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
   contents to include your merchant ID and an application name.

4. Build and run any of the included samples.

   You will need to enter your Google login in the terminal.
   If using an OAuth2 client ID for the first time, the application will then
   open a browser so you can authorize it to access your Merchant Center
   account.

5. Examine your shell output, be inspired and start hacking an amazing new app!
