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
# a single file in the configuration directory as a token store like other
# samples. Because we're only storing one token, we just ignore the id argument.

require 'googleauth/token_store'
require 'multi_json'

class TokenStore < Google::Auth::TokenStore
  def initialize(options = {})
    @filename = File.join(options[:config].path, 'stored-token.json')
  end

  def load(id)
    puts "Attempting to load stored OAuth2 token from #{@filename}."
    begin
      token = File.read(@filename)
    rescue
      # Any failure to retrieve the token will just mean that we
      # re-authenticate, so we can ignore any exceptions.
      return nil
    end
  end

  def store(id, token)
    begin
      File.open(@filename, "w") { |file| file << token }
    rescue
      # Similarly, failure to store the token just means that the
      # user will have to re-authenticate next time, so just let them
      # know we failed.
      puts "Failed to store the OAuth2 token, continuing."
    end
  end

  def delete(id)
    begin
      File.delete(@filename)
    rescue
      # Last, but not least, if the user of the TokenStore requests that
      # we delete the token but for some reason we fail, just note that.
      puts "Token deletion requested, but couldn't delete #{@filename}."
    end
  end
end
