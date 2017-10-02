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
# Contains an example workflow using the Productstatuses service.

require_relative '../shopping_common'
require_relative 'get_product_status'
require_relative 'list_product_statuses'

def product_status_workflow_nonmca(content_api, merchant_id)
  puts "Listing all product statuses for  MC #{merchant_id}:"
  list_product_statuses(content_api, merchant_id)
  puts
end

def product_status_workflow(content_api, config)
  puts 'Performing workflow for the Productstatuses service.'
  puts
  if config.is_mca
    puts 'MCAs do not contain products.'
    puts
  else
    product_status_workflow_nonmca(content_api, config.merchant_id)
  end
  puts 'Done with the Productstatuses workflow.'
end

if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)

  product_status_workflow(content_api, config)
end
