# Merchant API Java samples

Here's a basic example
of Google Shopping integration called from a command line application.

You can use this project to start your experimentation with
the Merchant API.

## Prerequisites

Make sure you're running Java 8+. If you use Maven, you can use the
included pom.xml to install the required dependencies. From
the directory of the pom.xml, run `mvn install`.

## Set up

See the top-level `README` for more information on
how to set up authentication and the common sample configuration. The rest
of this document assumes you've already set up authentication and the configuration.

## Run the samples

This section assumes you've already cloned the code and are reading from a local
directory. If not, clone the code to a local directory and set up the project
appropriately for access to the Google APIs Client Library for Java.

Build and run any of the included samples in your preferred IDE.

If using maven, to build and run your samples, navigate to the directory of
the pom.xml. Then run `mvn compile`.

If the code compiles succesfully, then run `mvn exec`. Use the following syntax
to run the `InsertRegionalInventorySample` file.

```
mvn exec:java -Dexec.mainClass="merchant.samples.inventories.InsertRegionalInventorySample"
```

Examine your shell output, be inspired and start working on an amazing new app!

We hope these samples give you the inspiration needed to create your new
application using Merchant API!