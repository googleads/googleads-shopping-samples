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
# Creates a TokenStore useable by the Google authentication library that uses
# the configuration file as a token store like other samples.
#
# We keep the token in "native" format inside the Config class, since
# other samples expect it to be the native format in the JSON file, so we'll
# need to load it from the string version passed in via store and to
# dump it from the native version that load is supposed to return.

require 'googleauth/token_store'
require 'multi_json'

class TokenStore < Google::Auth::TokenStore
  def initialize(options = {})
    @config = options[:config]
  end

  def load(id)
    if not @config.token.nil? and @config.email_address == id
      return MultiJson.dump(@config.token)
    else
      return nil
    end
  end

  # Store the email address as well, since the token should be keyed to that.
  # This means that only one authentication token can be stored at a
  # time, instead of being able to store one per auth ID, but it makes
  # life easier for trying out the samples quickly.
  def store(id, token)
    @config.email_address = id
    @config.token = MultiJson.load(token)
    @config.write()
  end

  def delete(id)
    @config.token = nil if @config.email_address == id
    @config.write()
  end
end
