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
# Contains common utilities for samples related to the Accountstatuses service.

require_relative "../shopping_common"

def print_account_status(status)
  puts "Information for account #{status.account_id}:"
  if status.products.nil?
    puts "- No data quality issues."
    return
  end
  issue_count = 0
  status.products.each do |product_issue|
    if product_issue.item_level_issues.nil?
      break
    end
    product_issue.item_level_issues.each do |issue|
      issue_count += 1
      puts "  - Issue: #{issue.code} '#{issue.detail}' "\
           "affecting #{issue.num_items} items"
    end
  end
  puts "- There are #{issue_count} data quality issue(s)."
end
