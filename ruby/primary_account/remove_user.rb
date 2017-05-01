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
# Removes a user from the primary account.

require_relative '../shopping_common'

def remove_user(content_api, merchant_id, email_address)
  # First we need to retrieve the full object, so we know the existing set of
  # users.
  # No point retrieving the entire object when we just want the list of users.
  account = content_api.get_account(
      merchant_id, merchant_id, fields: 'users'
  ) do |res, err|
    if err
      if err.status == 404
      puts "Account #{merchant_id} not found."
    else
      handle_errors(err)
      end
      exit
    end
  end

  # Remove user from user list.
  account.users.reject! { |user| user.email_address == email_address }

  # Patch account with new user list.
  response = content_api.patch_account(merchant_id, merchant_id, account) do
      |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts "User #{email_address} removed from account #{merchant_id}."
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  email_address = config.account_sample_user
  if email_address.nil? or email_address.empty?
    puts "No account sample user address in the configuration."
    exit
  end
  remove_user(content_api, config.merchant_id, email_address)
end
