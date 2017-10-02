#!/usr/bin/env ruby
# Encoding: utf-8
#
# Copyright:: Copyright 2017, Google Inc. All Rights Reserved.
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
# Contains an example workflow using the Products service.

require 'retriable'

require_relative '../shopping_common'
require_relative 'delete_product'
require_relative 'insert_product'
require_relative 'get_product'
require_relative 'list_products'

def product_workflow_nonmca(content_api, config)
  merchant_id = config.merchant_id
  puts "Listing products for MC #{merchant_id}:"
  list_products(content_api, merchant_id)
  puts

  info = insert_product(content_api, config)
  puts

  get_product(content_api, merchant_id, info.id)
  puts

  delete_product(content_api, merchant_id, info.id)
  puts
end

def product_workflow(content_api, config)
  puts 'Performing workflow for the Products service.'

  if config.is_mca
    puts 'MCAs do not contain products.'
    puts
  else
    product_workflow_nonmca(content_api, config)
  end

  puts 'Done with the Products workflow.'
end

if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)

  product_workflow(content_api, config)
end
