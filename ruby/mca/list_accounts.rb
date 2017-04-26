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

# The maximum number of results to be returned in a page.
MAX_PAGE_SIZE = 50

def list_accounts(content_api, merchant_id, next_page = nil)
  content_api.list_accounts(
      merchant_id, max_results: MAX_PAGE_SIZE, page_token: next_page
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
    list_accounts(content_api, merchant_id, res.next_page_token)
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config = Config.load(options.path)
  unless config.is_mca
    puts "Merchant in configuration is not described as an MCA."
    exit
  end
  content_api = service_setup(config)
  list_accounts(content_api, config.merchant_id)
end
