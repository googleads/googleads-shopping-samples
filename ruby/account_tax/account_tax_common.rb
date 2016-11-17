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
# Contains common utilities for samples related to the Accounttax service.

require_relative '../shopping_common'

def print_account_tax(settings)
  puts "Information for account #{settings.account_id}:"
  if settings.rules.nil?
    puts "- Tax is not being charged."
    return
  end
  puts "- There are #{settings.rules.length} taxation rule(s)."
  settings.rules.each do |rule|
    print "  - For location #{rule.location_id} "
    print "in country #{rule.country}: "
    unless rule.rate_percent.nil?
      puts "rate set to #{rule.rate_percent}%."
    end
    if rule.use_global_rate?
      puts "using the global tax table rate."
    end
    if rule.shipping_taxed?
      puts " Note: shipping charges are also taxed."
    end
  end
end

def create_sample_account_tax(account_id)
  return Google::Apis::ContentV2::AccountTax.new({
    :account_id => account_id,
    :rules => [
      Google::Apis::ContentV2::AccountTaxTaxRule.new({
        :country => "US",
        :location_id => 21167,
        :use_global_rate => true
      })
    ]
  })
end
