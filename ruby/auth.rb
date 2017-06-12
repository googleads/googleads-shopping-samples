#!/usr/bin/env ruby
# Encoding: utf-8
#
# Copyright:: Copyright 2017, Google Inc. All Rights Reserved.
#
# License:: Licensed under the Apache License, Version 2.0 (the "License");
#           you may not use this file except in compliance with the License.
#           You may obtain a copy of the License at
#
#           http://www.apache.org/licenses/LICENSE-2.0
#
#           Unless required by applicable law or agreed to in writing, software
#           distributed under the License is distributed on an "AS IS" BASIS,
#           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
#           implied.
#           See the License for the specific language governing permissions and
#           limitations under the License.
#
# Handles authentication across all Google Content API for Shopping samples.

require 'googleauth'
require 'googleauth/stores/file_token_store'
require 'launchy'
require 'multi_json'
require 'webrick'

API_SCOPE = 'https://www.googleapis.com/auth/content'
CALLBACK_URI = '/oauth2callback'
REDIRECT_PORT = 8080

def authenticate(config)
  # First try the Application Default Credentials before continuing.
  begin
    credentials = Google::Auth::get_application_default(scope=API_SCOPE)
    puts "Loaded Application Default Credentials."
    return credentials
  rescue StandardError
    # Unfortunately the ADC loader raises StandardError. Thus, we'll
    # just ignore any error here, and assume it means that the ADC
    # couldn't be loaded.
  end
  if config.path.nil?
    raise "Must use Application Default Credentials with no configuration."
  end
  # Check for both kinds of authentication. Let service accounts win, as
  # they're an easier flow to authenticate.
  service_account_file = File.join(config.path, "service-account.json")
  client_id_file = File.join(config.path, "client-secrets.json")
  if File.exist?(service_account_file)
    puts "Loading service account credentials from #{service_account_file}."
    return Google::Auth::DefaultCredentials.make_creds(
        scope: API_SCOPE,
        json_key_io: File.open(service_account_file))
  elsif File.exist?(client_id_file)
    puts "Loading OAuth2 client from #{client_id_file}."
    client_id = Google::Auth::ClientId.from_file(client_id_file)
    token_store = TokenStore.new(config: config)
    authorizer = Google::Auth::UserAuthorizer.new(
        client_id, API_SCOPE, token_store, callback_uri = CALLBACK_URI)

    # Since our token_store doesn't depend on the user credentials,
    # just pass in the empty string (since nil is not accepted).
    credentials = authorizer.get_credentials("")
    unless credentials.nil?
        puts "Successfully loaded stored OAuth2 token."
        return credentials
    end
    puts "No valid OAuth2 token found in storage, reauthorizing."
    base_url = "http://localhost:#{REDIRECT_PORT}"
    url = authorizer.get_authorization_url(base_url: base_url)
    code = fetch_code_from_url(url)
    return authorizer.get_and_store_credentials_from_code(
        user_id: '', code: code, base_url: base_url)
  end
  puts "No OAuth2 authentication credentials found. Checked:"
  puts "- Google Application Default Credentials"
  puts "- #{service_account_file}"
  puts "- #{client_id_file}"
  puts "Please read the accompanying README.md for instructions."
  exit
end

# Fetches the authorization code from Google by running a local HTTP
# server and launching the authorization process in a browser.
# After the user authenticates and authorizes the samples, the
# authorization process is redirected to the local HTTP server.
def fetch_code_from_url(url)
  code = nil
  server = WEBrick::HTTPServer.new({
    :AccessLog => [],
    :Logger => WEBrick::BasicLog.new(
        log_file=nil, level=WEBrick::BasicLog::FATAL),
    :Port => REDIRECT_PORT,
  })
  # We'll use a condition variable to signal that we've received the code.
  mutex = Mutex.new
  code_notification = ConditionVariable.new
  server.mount_proc(CALLBACK_URI) do
      |req, res|
    code = req.query["code"]
    res.status = 200
    res['Content-Type'] = 'text/plain'
    res.body = 'Authorization completed, you may close this window now.'
    mutex.synchronize { code_notification.signal }
  end
  Launchy.open(url)
  # Launch the HTTP server on another thread so it doesn't block
  # the samples and then wait until we have the code to shut down
  # the server and continue.
  server_thread = Thread.new { server.start }
  mutex.synchronize {
    code_notification.wait(mutex)
    server.shutdown
    server_thread.join
  }
  return code
end
