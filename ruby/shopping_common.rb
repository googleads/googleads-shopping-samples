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

require 'addressable/uri'
require 'google/apis/content_v2'
require 'google/api_client/client_secrets'
require 'googleauth'
require 'googleauth/stores/file_token_store'
require 'multi_json'

require_relative 'arg_parser'
require_relative 'config'
require_relative 'token_store'

ENDPOINT_ENV_VAR = 'GOOGLE_SHOPPING_SAMPLES_ENDPOINT'

API_NAME = 'content'
API_VERSION = 'v2'
API_SCOPE = 'https://www.googleapis.com/auth/content'

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
  puts "- #{service_account_file}"
  puts "- #{client_id_file}"
  puts "Please read the accompanying README.md for instructions."
  exit
end

# Handles configuration loading, authentication, and loading of the API.
#
# It takes the options parsed from the command line and optionally takes
# a boolean that determines whether the sandbox endpoint should be used.
#
# It returns both the configuration and the API service object.
def service_setup(options, use_sandbox = false)
  if options.noconfig
    config = Config.new()
  else
    config = Config.load(options.path)
  end
  credentials = authenticate(config)

  # Initialize API Service.
  service = Google::Apis::ContentV2::ShoppingContentService.new
  service.authorization = credentials
  if ENV[ENDPOINT_ENV_VAR]
    str = ENV[ENDPOINT_ENV_VAR]
    # Endpoint must end in a "/".
    str = str + "/" unless str.end_with? "/"
    uri = Addressable::URI.parse(str)
    unless uri.absolute?
      puts ("API endpoint must be absolute: #{uri}")
      exit
    end
    service.root_url = uri.site
    service.base_path = uri.path
    puts ("Using non-standard API endpoint: #{uri}")
  end
  service.client_options.application_name = "Content API for Shopping Samples"

  # Whether sandbox was requested or not, we need to make the calls
  # here against the normal service, since the sandbox only has access
  # to the methods in the Orders service.
  retrieve_configuration(service, config)

  if use_sandbox
    # Since there are no other needs for the regular service, we'll just
    # change it to point at the sandbox endpoint.
    uri = Addressable::URI.parse(service.root_url + service.base_path)
    if uri.basename == "v2"
      service.base_path = uri.join("../v2sandbox/").path
    else
      puts ("Using same API endpoint for sandbox service.")
      puts ("The Orders sample will fail unless it supports sandbox methods.")
    end
  end

  return config, service
end

# Fill in configuration fields using information from the Content API.
# If we have not yet received a merchant ID, we will also get that information.
def retrieve_configuration(service, config)
  service.get_account_authinfo() do |res, err|
    if err
      handle_errors(err)
      exit
    end

    if res.account_identifiers.nil?
      puts "Authenticated user has no access to any Merchant Center accounts."
      exit
    end

    if config.merchant_id.nil?
      first_account = res.account_identifiers[0]
      if first_account.merchant_id.nil?
        config.merchant_id = first_account.aggregator_id.to_i
      else
        config.merchant_id = first_account.merchant_id.to_i
      end
      puts "Running samples with Merchant Center #{config.merchant_id}."
    end

    config.is_mca = false
    res.account_identifiers.each do |account_id|
      # The configuration stores the merchant_id as a number, so
      # make sure to compare to the numerical value of these fields.
      break if account_id.merchant_id.to_i == config.merchant_id
      if account_id.aggregator_id.to_i == config.merchant_id
        config.is_mca = true
        break
      end
    end
    if config.is_mca
      puts "Merchant Center #{config.merchant_id} is an MCA."
    else
      puts "Merchant Center #{config.merchant_id} is not an MCA."
    end

    service.get_account(config.merchant_id, config.merchant_id) do |res, err|
      if err
        puts "The authenticated user cannot access MC #{config.merchant_id}."
        exit
      end

      config.website_url = res.website_url

      if config.website_url.nil?
        puts "Merchant Center #{config.merchant_id} has no website configured."
      else
        print "Website for Merchant Center #{config.merchant_id}: "
        puts config.website_url
      end
    end
  end
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
