# Content API for Shopping v2 Samples

A collection of command-line samples for the Content API for Shopping

##Installation and first request

1. Download Google APIs Client Library for Python (google-api-python-client):
  https://code.google.com/p/google-api-python-client/

  or use pip:

  ```bash
  $ pip install google-api-python-client
  ```

2. Make sure you can import the client library:

  ```
  $ python
  >>> import apiclient
  ```

3. Execute any of the scripts to begin the auth flow:

  ```bash
  $ python product_list.py
  ```

  A browser window will open and ask you to login.

4. Accept the permissions dialog. The browser should display

  `The authentication flow has completed.`

  Close the window and go back to the shell.
