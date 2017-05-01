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
# Updates the specified client account on the specified parent account.

require_relative 'mca_common'

def update_account(content_api, merchant_id, account_id)
  new_name = 'updated-account%s' % unique_id()
  patch_account = Google::Apis::ContentV2::Account.new({:name => new_name})

  content_api.patch_account(merchant_id, account_id, patch_account) do
      |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts 'Account successfully updated.'
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size == 1
    puts "Usage: #{$0} ACCOUNT_ID"
    exit
  end
  account_id = ARGV[0]

  config, content_api = service_setup(options)
  unless config.is_mca
    puts "Merchant in configuration is not described as an MCA."
    exit
  end
  update_account(content_api, config.merchant_id, account_id)
end
