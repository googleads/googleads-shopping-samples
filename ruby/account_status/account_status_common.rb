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

require_relative '../shopping_common'

def print_account_status(status)
  puts "Information for account #{status.account_id}:"
  if status.data_quality_issues.nil?
    puts "- No data quality issues."
    return
  end
  puts "- There are #{status.data_quality_issues.length} data quality issue(s)."
  status.data_quality_issues.each do |issue|
    puts "  - (#{issue.severity}) [#{issue.id}]:"
    print "    Affects #{issue.num_items} items, "
    puts "#{issue.example_items.length} example(s) follow."
    issue.example_items.each do |item|
      puts "    - Item #{item.item_id}: #{item.title}"
    end
  end
end
