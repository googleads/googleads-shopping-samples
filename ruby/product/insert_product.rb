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
# Adds a product to the specified account.

require_relative 'product_common'

def insert_product(content_api, config)
  example_id = 'book#%s' % unique_id()
  product = create_example_product(config, example_id)

  content_api.insert_product(config.merchant_id, product) do |result, err|
    if err
      handle_errors(err)
      exit
    end

    puts "Product created with ID #{result.id}."

    # Our example product has no product_type, so we should get
    # at least one warning.
    handle_warnings(result)
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  insert_product(content_api, config)
end
