# Google Content API for Shopping Ruby Samples

This is a set of simple samples written in Ruby, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you're running Ruby 2.0+ and you've installed the [Google
APIs Client Library for Ruby]
(https://developers.google.com/api-client-library/ruby/start/installation).

## Setup Authentication

Before getting started, check the Getting Started section on the [Ruby client
library documentation page]
(https://developers.google.com/api-client-library/ruby/start/get_started).

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.

1. Download your [OAuth 2.0 client ID]
   (console.developers.google.com/apis/credentials)
   to `content-oauth2.json` in the root of the code directory.

2. Change `'{USER ID HERE}'` in `shopping-common.rb` to the desired user ID.

3. Start up a sample:

        $ ruby product/list_products.rb

4. Complete the authorization steps on your browser.

5. Examine your shell output, be inspired and start hacking an amazing new app!
