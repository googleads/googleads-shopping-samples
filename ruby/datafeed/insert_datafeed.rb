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

  res = content_api.insert_datafeed(merchant_id, datafeed)

  puts "Datafeed created with ID #{res.id}."
  return res
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  begin
    insert_datafeed(content_api, config.merchant_id)
  rescue Exception => ex
    handle_errors(ex)
  end
end
