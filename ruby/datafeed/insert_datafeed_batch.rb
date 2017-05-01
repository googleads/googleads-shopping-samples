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
# Adds several datafeeds to the specified account, in a single batch.

require_relative 'datafeed_common'

BATCH_SIZE = 5

def insert_datafeed_batch(content_api, merchant_id)
  requests = (1..BATCH_SIZE).map do |n|
    example_id = 'feed%s' % unique_id()
    datafeed = create_example_datafeed(example_id)
    Google::Apis::ContentV2::DatafeedsBatchRequestEntry.new({
        :batch_id => n,
        :merchant_id => merchant_id,
        :datafeed => datafeed,
        :request_method => 'insert'})
  end

  batch_request =
      Google::Apis::ContentV2::BatchDatafeedsRequest.new({:entries => requests})

  content_api.batch_datafeed(batch_request) do |res, err|
    if err
      puts "Overall batch call resulted in an error."
      handle_errors(err)
      exit
    end

    res.entries.each do |batch_resp|
      if batch_resp.errors
        puts "Batch item #{batch_resp.batch_id} resulted in errors."
        batch_resp.errors.each do |sub_err|
          handle_errors(sub_err)
        end
      else
        puts "Batch item #{batch_resp.batch_id} successful."
        puts "Datafeed created with ID #{batch_resp.datafeed.id}."
      end
      puts
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  insert_datafeed_batch(content_api, config.merchant_id)
end
