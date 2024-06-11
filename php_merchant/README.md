# Google Merchant API for Shopping PHP Samples

This is a set of simple samples written in PHP, which provide a minimal
example of Google Shopping integration within a command line application.
These samples are written to be run as a command line application, not as a
webpage.

This starter project provides a great place to start your experimentation into
the Google Merchant API for Shopping.

A [Composer](https://getcomposer.org/) configuration has been included for
dependency management.

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up authentication on your local machine. The rest
of this document assumes you have created either a `client-secrets.json` or
`service-account.json` file in the correct configuration directory.

If you are using OAuth 2.0 Client IDs and secrets to get a new refresh token,
you need to run the following file (`GenerateUserCredentials.php`) from the root
directory, which will generate and store your refresh token, client id, and
client secret on your local machine in a file called `token.json`.

Below is an example of how to run the `GenerateUserCredentials.php` code sample
file.

    ```
    $ php examples/Authentication/GenerateUserCredentials.php # Runs a program to generate and store your on User Credentials on your local machine.
    ```

These samples expect you to authenticate by either having your refresh token
stored in your root directory on your local machine, or to be using a service
account to authenticate.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory. The instructions
below assume that you are using Composer.

1. Run Composer in the root directory to install the necessary dependencies.

   ```
   $ composer install
   ```

1. If you are using OAuth 2.0 Client IDs and secrets, ensure you've first ran
`GenerateUserCredentials.php` and you have a file called `token.json` on your
local machine. Once you have the `token.json` file or if you're using a service
account to authenticate, proceed to the next step.

1. Run one of the following samples on the command line from the root directory.
Below is an example of how to run the `InsertRegionalInventory.php` code
sample file.

    ```
    $ php examples/inventories/InsertRegionalInventory.php # Runs a program to insert a regional inventory.
    ```

1. Examine your shell output, be inspired and start hacking an amazing new app!
