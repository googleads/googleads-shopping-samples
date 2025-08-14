# Meet Merchant API - the official successor to Content API for Shopping

Check out the [Merchant API code samples repository](https://github.com/google/merchant-api-samples).

Get the latest on new [Merchant API](https://developers.google.com/merchant/api) features, bug fixes, and updates.

[Get started with Merchant API](https://developers.google.com/merchant/api/guides/quickstart) and get access to data, insights, and unique capabilities at scale.





# Samples for the Content API for Shopping and Manufacturer Center API

These code samples are organized by platform or language. Each language
directory contains a `README` with more information about how to run the
samples for that particular language.  Here, we cover setting up
authentication and the common configuration file used by all the samples.

For more information on the APIs, please refer to the documentation for the
[Content API for Shopping](https://developers.google.com/shopping-content/)
and the
[Manufacturer Center API](https://developers.google.com/manufacturers/).

## Choose Your Method of Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/guides/how-tos/service-accounts)
instead to simplify the authentication flow. These samples also support using
[Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).

Setting up authentication for the Manufacturer Center API is similar to the
Content API. Just make sure to select the Manufacturer Center API in the API
Console. Both APIs can be selected if you wish to try samples for both APIs.

## Setting up Authentication and Sample Configuration

1.  Create the directory `$(HOME)/shopping-samples` to store the
    configuration.

    If you are unsure where this will be located in your particular setup, then
    run the samples (following the language-specific `README`). Errors
    from the samples related to either this directory or necessary files not
    existing will provide the full path to the expected directory/files.

    Within this directory, also create the following subdirectories, depending
    on which API you would like to try:

    * `content` for the Content API for Shopping
    * `manufacturers` for the Manufacturer Center API

    Place the files described below in the appropriate subdirectory for the API
    you want to try.

2.  Set up your desired authentication method.

    If you are using Google Application Default Credentials:

    *   Follow the directions on the [Google Application Default
        Credentials](https://developers.google.com/identity/protocols/application-default-credentials)
        page.

    If you are using a service account:

    *   Put the JSON file you downloaded when creating the service account to
        the file `service-account.json` in the appropriate API configuration
        subdirectories.

    If you are using an OAuth2 client ID:

    *   Download your [OAuth2 client
        credentials](https://console.developers.google.com/apis/credentials) to
        the file `client-secrets.json` in the appropriate API configuration
        subdirectories.

        **Note:** The samples assume that you are using an OAuth2 client ID that
        can use a loopback IP address to retrieve tokens. If you are not or are
        unsure that you are, please visit the
        [OAuth2.0 for Mobile & Desktop Apps]
        (https://developers.google.com/identity/protocols/OAuth2InstalledApp)
        page and follow the instructions there to create a new OAuth2 client ID
        to use with the samples.

    You can set up multiple authentication methods to try out different flows,
    but note that the samples will always use the first credentials that can be
    loaded, in the order:

    1.  [Google Application Default
        Credentials](https://developers.google.com/identity/protocols/application-default-credentials)
    2.  [Service
        accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
        credentials
    3.  [OAuth2
        client](https://developers.google.com/shopping-content/v2/how-tos/authorizing)
        credentials

3.  Each set of samples uses a different configuration file.  For the Content
    API for Shopping, take the example `merchant-info.json` from the repository
    root and copy it into `$(HOME)/shopping-samples/content`.  Next, change its
    contents appropriately. It contains a JSON object with the following fields:

    | Field                     | Type   | Description                                    |
    |---------------------------|--------|------------------------------------------------|
    | `merchantId`              | number | The Merchant Center ID to run samples against. |
    | `accountSampleUser`       | string | If non-empty, the email address for the user to add/remove in samples for the `Accounts` service. |
    | `accountSampleAdWordsCID` | number | If non-zero, the AdWords Customer ID to link/unlink in samples for the `Accounts` service. |

    For the Manufacturer Center API, take the example `manufacturer-info.json`
    from the repository root and copy it into
    `$(HOME)/shopping-samples/manufacturers`.  Next, change its contents
    appropriately. It contains a JSON object with the following fields:

    | Field                     | Type   | Description                                    |
    |---------------------------|--------|------------------------------------------------|
    | `manufacturerId`          | number | The Manufacturer Center ID to run samples against. |
    | `websiteUrl`              | string | The URL (without trailing slash) associated with the Manufacturer Center account. |

    If using OAuth2 client credentials, once you have authorized access, your
    token details will be stored in the `stored-token.json` file in the samples
    configuration directory. If you have any issues authenticating, remove this
    file and you will be asked to re-authorize access.

## Try Out the Samples

Now that you've configured both the common sample configuration file and set up
your authentication credentials, it's time to build and run any of the included
samples.  As mentioned before, there are language-specific instructions in
the `README`s located in each language subdirectory. Have fun!

## Possible Issues

* When using the Content API for Shopping, if you haven't set up tax settings on
  your account, you may get an error when running certain samples. If you
  receive a "missing tax settings" error, set your tax settings in the Merchant
  Center before trying these samples.  Selecting the "Don't charge taxes in the
  United States" setting is sufficient.
