# Google Content API for Shopping Python Samples

This is a set of simple samples written in Python, which provide a minimal
example of Google Shopping integration within a command line application.

This starter project provides a great place to start your experimentation into
the Google Content API for Shopping.

## Prerequisites

Please make sure that you've installed the
[Google APIs Client Library for Python](https://developers.google.com/api-client-library/python/start/installation).
After installing, make sure you can import the client library:

  ```
  $ python
  >>> import googleapiclient
  ```

## Setup Authentication and Sample Configuration

If you have not already done so, please read the top-level `README` to discover
how to set up both authentication and the common sample configuration.  The rest
of this document assumes you have performed both tasks.

## Running the Samples

We are assuming you've checked out the code and are reading this from a local
directory. If not, check out the code to a local directory.

1. Start up a sample:

        $ python product_list.py

   If using an OAuth2 client ID for the first time, the application will
   open a browser automatically so you can agree to the OAuth2 access.
   The resulting access token will be stored in the sample configuration in
   the `token` field, so if you run into authentication failures later, you
   can remove that field to re-authorize access.

3. Examine your shell output, be inspired and start hacking an amazing new app!
