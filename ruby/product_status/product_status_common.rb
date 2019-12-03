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
# Contains common utilities for samples related to the Productstatuses service.

require_relative "../shopping_common"

def print_product_status(status)
  puts "Information for product #{status.product_id}:"
  puts "- Title: #{status.title}"
  puts "- Destination statuses:"
  status.destination_statuses.each do |s|
    puts "  - Destination #{s.destination}: #{s.status}"
  end
  puts "- There are #{status.item_level_issues.length} issue(s)."
  status.item_level_issues.each do |issue|
    puts "  - Code: #{issue.code}"
    puts "    Description: #{issue.description}"
    puts "    Detailed description: #{issue.detail}"
    puts "    Resolution: #{issue.resolution}"
    puts "    Servability: #{issue.servability}"
  end
end
