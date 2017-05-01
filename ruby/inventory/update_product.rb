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
# Updates the specified product on the specified account using the inventory
# collection.
# If you're updating any of the supported properties in a product, be sure to
# use the inventory.set method, for performance reasons.

require_relative '../shopping_common'

def update_product(content_api, merchant_id, product_id)
  new_status = {
    availability: 'out of stock',
    price: Google::Apis::ContentV2::Price.new({
        :value => '3.00',
        :currency => 'USD'})
  }

  inv_request = Google::Apis::ContentV2::SetInventoryRequest.new new_status

  store_code = product_id.split(':').first
  response = content_api.set_inventory(
      merchant_id, store_code, product_id, inv_request
  ) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts 'Product successfully updated.'
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size == 1
    puts "Usage: #{$0} PRODUCT_ID"
    exit
  end
  product_id = ARGV[0]

  config, content_api = service_setup(options)
  update_product(content_api, config.merchant_id, product_id)
end
