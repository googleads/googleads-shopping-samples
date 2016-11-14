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

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory and set up the
project appropriately for access to the Google APIs Client Library for Java.

1. Build and run any of the included samples.

   If using an OAuth2 client ID for the first time, the application will open a
   browser so you can authorize it to access your Merchant Center account. The
   access token will be stored in the sample configuration file, so if you have
   authentication issues, delete the `token` field and reauthenticate.

2. Examine your shell output, be inspired and start hacking an amazing new app!
