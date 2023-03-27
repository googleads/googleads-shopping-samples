# Merchant API code samples

The Merchant API is a redesign of the Content API for Shopping. 
All code samples for the Merchant API are located in this directory.

Code examples are available in the following programming languages:

* Java

Each language directory contains a `README` with more information about how to
run the samples for that particular language.

## Authentication

Currently our samples only support using
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
for authentication.

You can also use other
[OAuth](https://developers.google.com/shopping-content/guides/how-tos/authorizing)
methods to authenticate, but those ways are not explicitly demonstrated in
these samples.

## Set up

The following configuration is backwards compatible with 
Content API for Shopping authentication.


1.  Create the directory `${HOME}/shopping-samples` to store the
    configuration.

    If you are unsure where this will be located in your particular setup, then
    run the samples (following the language-specific `README`). Errors
    from the samples related to either this directory or necessary files not
    existing will provide the full path to the expected directory/files.

    Within this directory, also create the following subdirectory:

    * `content` for the Merchant API

    Place the files described below in the `content` subdirectory.

2.  Set up your desired authentication method.

    If you are using a service account:

    *   Copy or move the JSON file you downloaded when creating the service
        account to:

        ```
        `${HOME}/shopping-samples/content/service-account.json`
        ```

3.  For the Merchant API, take the example `merchant-info.json` from
    the repository
    root and copy it into `$(HOME)/shopping-samples/content`.  Next, change its
    contents appropriately. It contains a JSON object with the following fields:

    | Field                     | Type   | Description                                    |
    |---------------------------|--------|------------------------------------------------|
    | `merchantId`              | number | The Merchant Center ID to run samples against. |



Build and run any of the samples to verify your authentication and sample configuration.

See the `README` in each language subdirectory for more information on that language.
