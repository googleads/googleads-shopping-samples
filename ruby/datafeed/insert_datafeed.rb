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
# Adds a datafeed to the specified account.

require_relative 'datafeed_common'

def insert_datafeed(content_api, merchant_id)
  example_id = 'feed%s' % unique_id()
  datafeed = create_example_datafeed(example_id)

  content_api.insert_datafeed(merchant_id, datafeed) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts "Datafeed created with ID #{res.id}."

    handle_warnings(res)
  end
end


if __FILE__ == $0
  unless ARGV.size == 1
    puts "Usage: #{$0} MERCHANT_ID"
    exit
  end
  merchant_id = ARGV[0]

  content_api = service_setup()
  insert_datafeed(content_api, merchant_id)
end
