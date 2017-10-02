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
# Gets all products on the specified account.

require_relative 'product_common'

def list_products(
    content_api, merchant_id, next_page = nil, count = 0, page_size = 50)
  content_api.list_products(
    merchant_id,
    max_results: page_size,
    page_token: next_page
  ) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    unless res.resources
      puts 'No results.'
      return
    end

    # Fetch all items in a loop. We limit to looping just 3 times for this
    # example as it may take a long time to finish if the account has many
    # products.  We'll use the optional count parameter for this.
    res.resources.each do |product|
      puts "#{product.id} #{product.title}"
      handle_warnings(product)
    end

    return if !res.next_page_token || count >= 3
    list_products(
        content_api, merchant_id, res.next_page_token, count + 1, page_size)
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  list_products(content_api, config.merchant_id)
end
