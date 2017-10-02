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
# Gets all client accounts on the specified parent account.

require_relative 'mca_common'

def list_accounts(content_api, merchant_id, next_page = nil, page_size = 50)
  content_api.list_accounts(
      merchant_id, max_results: page_size, page_token: next_page
  ) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    unless res.resources
      puts 'No results.'
      return
    end

    res.resources.each do |account|
      puts "#{account.id} #{account.name}"
    end

    return unless res.next_page_token
    list_accounts(content_api, merchant_id, res.next_page_token, page_size)
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  unless config.is_mca
    puts "Merchant in configuration is not described as an MCA."
    exit
  end
  list_accounts(content_api, config.merchant_id)
end
