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
# Runs all the (non-Orders) service workflow samples.

require_relative 'account_status/account_status_workflow.rb'
require_relative 'account_tax/account_tax_workflow.rb'
require_relative 'datafeed/datafeed_workflow.rb'
require_relative 'mca/account_workflow.rb'
require_relative 'product/product_workflow.rb'
require_relative 'product_status/product_status_workflow.rb'
require_relative 'shipping_settings/shipping_settings_workflow.rb'

if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)

  puts '-----------------------------'
  account_workflow(content_api, config)
  puts '-----------------------------'
  account_status_workflow(content_api, config)
  puts '-----------------------------'
  account_tax_workflow(content_api, config)
  puts '-----------------------------'
  datafeed_workflow(content_api, config)
  puts '-----------------------------'
  product_workflow(content_api, config)
  puts '-----------------------------'
  product_status_workflow(content_api, config)
  puts '-----------------------------'
  shipping_settings_workflow(content_api, config)
  puts '-----------------------------'
end
