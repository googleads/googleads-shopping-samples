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
# Gets the account information for the specified account.

require_relative 'account_status_common'

def get_account_status(content_api, merchant_id, account_id)
  content_api.get_account_status(merchant_id, account_id) do |res, err|
    if err
      handle_errors(err)
      exit
    end
    print_account_status(res)
  end
end

if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size == 1
    puts "Usage: #{$0} ACCOUNT_ID"
    exit
  end
  account_id = ARGV[0]

  config = Config.load(options.path)
  unless account_id == config.merchant_id.to_s or config.is_mca
    puts "Non-MCA merchant center accounts can only get their own information."
    exit
  end
  content_api = service_setup(config)
  get_account_status(content_api, config.merchant_id, account_id)
end
