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
# Deletes several datafeeds from the specified account, in a single batch.

require_relative 'datafeed_common'

def delete_datafeed_batch(content_api, merchant_id, datafeed_ids)
  batch_id = 0
  requests = datafeed_ids.map do |datafeed_id|
    batch_id += 1
    Google::Apis::ContentV2::DatafeedsBatchRequestEntry.new({
        :batch_id => batch_id,
        :merchant_id => merchant_id,
        :datafeed_id => datafeed_id,
        :request_method => 'delete'})
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
      end
      puts
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size >= 1
    puts "Usage: #{$0} DATAFEED_ID_1 [DATAFEED_ID_2 ...]"
    exit
  end
  datafeed_ids = ARGV

  config, content_api = service_setup(options)
  delete_datafeed_batch(content_api, config.merchant_id, datafeed_ids)
end
