# Samples for the Content API for Shopping

These code samples are organized by platform or language. Each language
directory contains a `README` with more information about how to run the
samples for that particular language.  Here, we cover setting up
authentication and the common configuration file used by all the samples.

For more information on the API, please refer to
[the documentation](https://developers.google.com/shopping-content/).

## Choose Your Method of Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
instead to simplify the authentication flow. These samples also support using
[Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).

## Setting up Authentication and Sample Configuration

1.  Create the directory `$(HOME)/.shopping-content-samples` to store the
    configuration.

    If you are unsure where this will be located in your particular setup, then
    run the samples (following the language-specific `README`). Errors
    from the samples related to either this directory or necessary files not
    existing will provide the full path to the expected directory/files.

2.  Set up your desired authentication method.

    If you are using Google Application Default Credentials:

    *   Follow the directions on the [Google Application Default
        Credentials](https://developers.google.com/identity/protocols/application-default-credentials)
        page.

    If you are using a service account:

    *   Put the JSON file you downloaded when creating the service account in
        `$(HOME)/.shopping-content-samples` with the filename
        `content-service.json`.

    If you are using an OAuth2 client ID:

    *   Download your [OAuth2 client
        credentials](https://console.developers.google.com/apis/credentials) to
        the file `content-oauth2.json` in `$(HOME)/.shopping-content-samples`.

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

3.  Take the example `merchant-info.json` from the repository root, copy it into
    `$(HOME)/.shopping-content-samples`, and then change its contents
    appropriately. It contains a JSON object with the following fields:

    | Field                     | Type   | Description                                    |
    |---------------------------|--------|------------------------------------------------|
    | `merchantId`              | number | The Merchant Center ID to run samples against. |
    | `applicationName`         | string | The name of the application.                   |
    | `emailAddress`            | string | What email address should be used for authentication. Setting this is required only if using OAuth2 client credentials. |
    | `websiteUrl`              | string | The URL (without trailing slash) associated with the Merchant Center account. |
    | `accountSampleUser`       | string | If non-empty, the email address for the user to add/remove in samples for the `Accounts` service. |
    | `accountSampleAdWordsCID` | number | If non-zero, the AdWords Customer ID to link/unlink in samples for the `Accounts` service. |
    | `isMCA`                   | bool   | Whether or not the Merchant Center account associated with `merchantId` is a multi-client account. |

    If using OAuth2 client credentials, once you have authorized access your
    token details will be stored in the `token` field. If you have any issues
    authenticating, remove this field and you will be asked to re-authorize
    access.

## Try Out the Samples

Now that you've configured both the common sample configuration file and set up
your authentication credentials, it's time to build and run any of the included
samples.  As mentioned before, there are language-specific instructions in
the `README`s located in each language subdirectory.

