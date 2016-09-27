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
# Handles common tasks across all datafeed samples.

require_relative '../shopping_common'

# Creates an example datafeed with the provided name.
def create_example_datafeed(name)
  # You can schedule monthly, weekly or daily.
  #
  # Monthly - set day of month (`day_of_month`) and hour (`hour`)
  # Weekly - set day of week (`weekday`) and hour (`hour`)
  # Daily - set just the hour (`hour`)
  fetch_schedule = {
    :weekday => 'monday',
    :hour => 6,
    :time_zone => 'America/Los_Angeles',
    :fetch_url => 'https://feeds.myshop.com/' + name
  }
  format = {
    :file_encoding => 'utf-8',
    :column_delimiter => 'tab',
    :quoting_mode => 'value quoting'
  }
  datafeed = {
    :name => name,
    :content_type => 'products',
    :attribute_language => 'en',
    :content_language => CONTENT_LANGUAGE,
    :intended_destinations => ['Shopping'],
    # The file name must be unique per account. We only use unique names in
    # these examples, so it's not an issue here.
    :file_name => name,
    :target_country => TARGET_COUNTRY,
    :fetch_schedule =>
        Google::Apis::ContentV2::DatafeedFetchSchedule.new(fetch_schedule),
    :format => Google::Apis::ContentV2::DatafeedFormat.new(format)
  }

  return Google::Apis::ContentV2::Datafeed.new datafeed
end
