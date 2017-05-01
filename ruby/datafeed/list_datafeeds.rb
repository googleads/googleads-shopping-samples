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
# Gets all datafeeds on the specified account.

require_relative 'datafeed_common'

def list_datafeeds(content_api, merchant_id)
  # There is a low limit on the number of datafeeds per account, so the list
  # method always returns all datafeeds.
  content_api.list_datafeeds(merchant_id) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    if res && res.resources
      res.resources.each do |datafeed|
        puts "#{datafeed.id} #{datafeed.name}"
      end
    else
      puts 'No results.'
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  list_datafeeds(content_api, config.merchant_id)
end
