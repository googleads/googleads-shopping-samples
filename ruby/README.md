# Google Content API for Shopping Ruby Samples

This is a set of simple samples written in Ruby, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you're running Ruby 2.0+ and you've installed the
[Google APIs Client Library for Ruby]
(https://developers.google.com/api-client-library/ruby/start/installation).

The Ruby samples also use the
[Launchy](https://github.com/copiousfreetime/launchy) gem to launch a browser
when requesting authorization with an OAuth2 client ID.

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.

1. Start up a sample:

        $ ruby product/list_products.rb

   If using an OAuth2 client ID for the first time, you will need to access
   the authorization URL printed on the terminal in your browser, authorize
   the access, and then paste the resulting code back into the terminal.
   The resulting access token will be stored in your `merchant-info.json`
   configuration, so if you have authentication issues, delete the `token`
   field and reauthenticate.

2. Examine your shell output, be inspired and start hacking an amazing new app!
