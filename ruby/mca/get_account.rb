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
# Retrieves information about a client account for the specified parent account.

require_relative 'mca_common'

def get_account(content_api, merchant_id, account_id)
  account = content_api.get_account(merchant_id, account_id)
  name = account.name
  id = account.id
  puts "Retrieved account '#{name}' (ID #{id}) for merchant #{merchant_id}"
  return account
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size <= 1
    puts "Usage: #{$0} [ACCOUNT_ID]"
    exit
  end

  config, content_api = service_setup(options)
  if ARGV.empty?
    account_id = config.merchant_id
  else
    account_id = ARGV[0].to_i
  end

  # Account.get can be used in one of two cases:
  # - The requested MCID is the same as the MCID we're using for the call.
  # - The merchant center account is an MCA.
  # The Merchant ID from JSON could be a number instead of a string, but here
  # we check against the string from the command line, so convert
  # the config version to a string for the equality check.
  unless account_id == config.merchant_id or config.is_mca
    puts "Non-MCA merchants cannot retrieve information about other accounts."
    exit
  end
  begin
    get_account(content_api, config.merchant_id, account_id)
  rescue Exception => ex
    handle_errors(ex)
  end
end
