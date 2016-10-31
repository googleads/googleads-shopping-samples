#!/usr/bin/env ruby
# Encoding: utf-8
#
# Copyright:: Copyright 2016, Google Inc. All Rights Reserved.
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
# Handles common tasks across all Google Content API for Shopping samples.

require 'google/apis/content_v2'
require 'google/api_client/client_secrets'
require 'googleauth'
require 'googleauth/stores/file_token_store'
require 'multi_json'

require_relative 'config'
require_relative 'token_store'

API_NAME = 'content'
API_VERSION = 'v2'
API_SCOPE = 'https://www.googleapis.com/auth/content'
CLIENT_ID_FILE = File.join(Config.path, "#{API_NAME}-oauth2.json")
SERVICE_ACCOUNT_FILE = File.join(Config.path, "#{API_NAME}-service.json")

# These constants define the identifiers for all of our example products/feeds.
#
# The products will be sold online.
CHANNEL = 'online'
# The product details are provided in English.
CONTENT_LANGUAGE = 'en'
# The products are sold in the USA.
TARGET_COUNTRY = 'US';

OOB_URI = 'urn:ietf:wg:oauth:2.0:oob'

def authenticate(config)
  # First try the Application Default Credentials before continuing.
  begin
    credentials = Google::Auth::get_application_default(scope=API_SCOPE)
    puts "Loaded Application Default Credentials."
    return credentials
  rescue
    # Unfortunately the ADC loader raises StandardError. Thus, we'll
    # just ignore any error here, and assume it means that the ADC
    # couldn't be loaded.
  end
  # Check for both kinds of authentication. Let service accounts win, as
  # they're an easier flow to authenticate.
  if File.exist?(SERVICE_ACCOUNT_FILE)
    puts "Loading service account credentials from #{SERVICE_ACCOUNT_FILE}."
    return Google::Auth::DefaultCredentials.make_creds(
        scope: API_SCOPE,
        json_key_io: File.open(SERVICE_ACCOUNT_FILE))
  elsif File.exist?(CLIENT_ID_FILE)
    puts "Loading OAuth2 client from #{CLIENT_ID_FILE}."
    client_id = Google::Auth::ClientId.from_file(CLIENT_ID_FILE)
    token_store = TokenStore.new(config: config)
    authorizer = Google::Auth::UserAuthorizer.new(
        client_id, API_SCOPE, token_store)
    user_id = config.email_address

    credentials = authorizer.get_credentials(user_id)
    unless credentials.nil?
      return credentials
    end
    url = authorizer.get_authorization_url(base_url: OOB_URI)
    puts "Open #{url} in your browser and enter the resulting code:"
    code = STDIN.gets
    return authorizer.get_and_store_credentials_from_code(
        user_id: user_id, code: code, base_url: OOB_URI)
  end
  puts "No OAuth2 authentication credentials found. Checked:"
  puts "- Google Application Default Credentials"
  puts "- #{SERVICE_ACCOUNT_FILE}"
  puts "- #{CLIENT_ID_FILE}"
  puts "Please read the accompanying README.md for instructions."
  exit
end

# Handles authentication and loading of the API.
def service_setup(config)
  credentials = authenticate(config)

  # Initialize API Service.
  service = Google::Apis::ContentV2::ShoppingContentService.new
  service.authorization = credentials

  return service
end

# Generates a unique ID based on the current UNIX timestamp and a runtime
# increment.
def unique_id
  $unique_id_increment = ($unique_id_increment || 0) + 1
  return (Time.new.to_f * 1000).to_i.to_s + $unique_id_increment.to_s
end

# Displays errors present in an API response.
def handle_errors(err)
  unless err && err.kind_of?(Google::Apis::Error)
    print "Unknown error: #{err}"
    exit
  end
  body_json = MultiJson.load(err.body)
  if body_json['error']
    puts "Error(s) when performing request:"
    body_json['error']['errors'].each do |error|
      puts "  - [#{error['reason']}] #{error['message']}"
    end
  else
    puts "Unknown error when performing request, details below:"
    puts
    puts "Response headers:"
    puts "-----------------"
    err.header.each do |key, value|
      puts "#{key}: #{value}"
    end
    puts
    puts "Response body:"
    puts "--------------"
    puts err.body
  end
end

# Displays warnings in an API response, if any.
def handle_warnings(response)
  warnings = response.warnings

  if warnings and warnings.size > 0
    puts "Warning(s) when performing request:"
    warnings.each do |warning|
      puts "  - [#{warning.reason}] #{warning.message}"
    end
  end
end
