# Google Merchant API Samples

This is a set of simple samples written in Java, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Merchant API.

## Prerequisites

Please make sure that you're running Java 8+. If you use Maven, you can use
the included `pom.xml` to install the required dependencies with the
`mvn install` command. Otherwise, install the
[Merchant API Client Library for Java](https://developers.google.com/merchant/api/client-libraries).

> **Note**
> The Merchant API Client Library for Java is separate for each bundle you use
> (e.g. Datasource is one client library, Products is another, etc.), and you
> need to install each client library separately.

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

Note that for the non-service account OAuth2 flow, the application will read
and store the created OAuth2 credentials from `$(HOME)/shopping-samples/content`
in a file called `token.json`.

If your refresh token is
[revoked or expired for any reason](https://developers.google.com/identity/protocols/oauth2#expiration),
you can try deleting the `token.json` file, then re-running the sample code
to create and save a new refresh token to see if it fixes your error.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory and set up the
project appropriately for access to the Google APIs Client Library for Java.

This section assumes you've already cloned the code and are reading from a local
directory. If not, clone the code to a local directory,  and set up the project
appropriately for access to the Google APIs Client Library for Java 
(see the prerequisities section above).

Build and run any of the included samples in your preferred IDE.

If using maven, to build and run your samples, navigate to the directory of
the pom.xml. Then run `mvn compile`.

If the code compiles succesfully, then run `mvn exec`, followed by the name of
the sample you wish to execute. The specific syntax is shown in the examples
below.

### Merchant API
Use the following syntax
to run the `InsertRegionalInventorySample` class, for example.

```
mvn exec:java -Dexec.mainClass="shopping.merchant.samples.inventories.v1beta.InsertRegionalInventorySample"
```

Examine your shell output, be inspired and start working on an amazing new app!

We hope these samples give you the inspiration needed to create your new
application!