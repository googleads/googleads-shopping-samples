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

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.  The instructions
below assume that you are using Composer.

1. Run Composer in the root directory to install the necessary dependencies.

   ```
   $ composer install
   ```

1. Run one of the following samples on the command line.

    ```
    $ php php/NonOrdersSamples.php # Runs through all non-Orders services.
    $ php php/OrdersSample.php     # Runs only the Orders service.
    ```

1. Examine your shell output, be inspired and start hacking an amazing new app!
