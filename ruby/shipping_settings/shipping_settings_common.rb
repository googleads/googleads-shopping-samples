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
# Contains common utilities for samples related to the Shippingsettings service.

require_relative '../shopping_common'

def print_shipping_settings(settings)
  puts "Information for account #{settings.account_id}:"
  if settings.postal_code_groups.nil?
    puts "- There are no postal code groups."
  else
    size = settings.postal_code_groups.length
    puts "- There are #{size} postal code group(s)."
    settings.postal_code_groups.each do |group|
      puts "  Postal code group \"#{group.name}\":"
      puts "  - Country: #{group.country}"
      puts "  - Contains #{group.postal_code_ranges.length} postal codes."
    end
  end
  if settings.services.nil?
    puts "- There are no service groups."
  else
    puts "- There are #{settings.services.length} shipping service(s)."
    settings.services.each do |service|
      puts "  Service \"#{service.name}\":"
      puts "  - Active: #{service.active}"
      puts "  - Country: #{service.delivery_country}"
      puts "  - Currency: #{service.currency}"
      min_days = service.delivery_time.min_transit_time_in_days
      max_days = service.delivery_time.max_transit_time_in_days
      puts "  - Delivery time: #{min_days} - #{max_days} days"
      puts "  - #{service.rate_groups.length} rate group(s) in this service."
    end
  end
end

def create_sample_shipping_settings()
  return Google::Apis::ContentV2::ShippingSettings.new({
    :postal_code_groups => [],
    :services => [
      Google::Apis::ContentV2::Service.new({
        :active => true,
        :currency => "USD",
        :delivery_country => "US",
        :delivery_time => {
          :min_transit_time_in_days => 3,
          :max_transit_time_in_days => 7
        },
        :name => "USPS",
        :rate_groups => [{
          :single_value => {
            :flat_rate => { :value => "5.00", :currency => "USD" }
          }
        }]
      })
    ]
  })
end
