# Google Content API for Shopping C#/.NET Samples

This is a set of simple samples written in C#/.NET, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you're using either

* [Visual Studio](https://www.visualstudio.com/),
* [Xamarin Studio](https://www.xamarin.com/studio), or
* [MonoDevelop](http://www.monodevelop.com/)

with [NuGet package management](https://www.nuget.org/).
NuGet will handle getting all the dependencies for you with the package
configuration in these projects.

## Setup Authentication

Before getting started, check the Getting Started section of the
[Content API for Shopping documentation](https://developers.google.com/shopping-content/v2/quickstart).
You may want to use
[service accounts](https://developers.google.com/shopping-content/v2/how-tos/service-accounts)
instead to simplify the authentication flow. These samples also support using
[Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory and load up the
solution. You will need to
[Restore NuGet Packages](https://docs.nuget.org/ndocs/consume-packages/package-restore)
as well to pull in the dependencies. (The IDEs listed above all support this.)

1. Create the directory `$(HOME)/.shopping-content-samples` to store the
   needed configuration.  On Windows, this will need to be in your
   personal "My Documents" folder.  Errors from the samples related to
   this directory not existing or files within not existing will provide the
   full path to the expected directory/files.

2. Set up your desired authentication method.

   If you are using Google Application Default Credentials:

   * Follow the directions on the [Google Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials) page.

   If you are using a service account:

    * Put the JSON file you downloaded when creating the service account in
      `$(HOME)/.shopping-content-samples` with the filename
      `content-service.json`.

   If you are using an OAuth2 client ID:

   * Download your [OAuth2 client credentials](https://console.developers.google.com/apis/credentials)
     to `content-oauth2.json` in `$(HOME)/.shopping-content-samples`.

   You can set up multiple authentication methods to try out different flows,
   but note that the samples will always use the first credentials that can
   be loaded, in the order:

   1. Application Default Credentials
   2. Service account credentials
   3. OAuth2 client credentials

3. Take the example `merchant-info.json` from the project root, copy
   it into `$(HOME)/.shopping-content-samples`, and then change its
   contents to include your Google login, merchant ID and an application
   name.  If you wish to try the primary account samples
   (AdWords linking/User adding), change the null for those fields
   to an appropriate string.

4. Build and run any of the included samples.

   If using an OAuth2 client ID for the first time, the application will then
   open a browser so you can authorize it to access your Merchant Center
   account.

5. Examine your shell output, be inspired and start hacking an amazing new app!
