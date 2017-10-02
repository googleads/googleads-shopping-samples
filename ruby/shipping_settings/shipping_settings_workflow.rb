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
# Contains an example workflow using the Shippingsettings service.

require_relative '../shopping_common'
require_relative 'get_shipping_settings'
require_relative 'list_shipping_settings'

def shipping_settings_workflow_common(content_api, merchant_id)
  puts "Getting shipping settings for MC #{merchant_id}:"
  get_shipping_settings(content_api, merchant_id, merchant_id)
  puts
end

def shipping_settings_workflow_mca(content_api, merchant_id)
  puts "Listing shipping settings for sub-accounts of MC #{merchant_id}:"
  list_shipping_settings(content_api, merchant_id)
  puts
end

def shipping_settings_workflow(content_api, config)
  puts 'Performing workflow for the Shippingsettings service.'
  puts
  shipping_settings_workflow_common(content_api, config.merchant_id)
  if config.is_mca
    shipping_settings_workflow_mca(content_api, config.merchant_id)
  end
  puts 'Done with the Shippingsettings workflow.'
end

if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)

  shipping_settings_workflow(content_api, config)
end
