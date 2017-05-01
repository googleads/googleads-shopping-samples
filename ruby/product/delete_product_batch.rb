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
# Deletes several products from the specified account, in a single batch.

require_relative 'product_common'

def delete_product_batch(content_api, merchant_id, product_ids)
  batch_id = 0
  requests = product_ids.map do |product_id|
    batch_id += 1
    Google::Apis::ContentV2::ProductsBatchRequestEntry.new({
        :merchant_id => merchant_id,
        :product_id => product_id,
        :request_method => 'delete',
        :batch_id => batch_id})
  end

  batch_request =
      Google::Apis::ContentV2::BatchProductsRequest.new({:entries => requests})

  content_api.batch_product(batch_request) do |res, err|
    # Error in the batch call itself.
    if err
      handle_errors(err)
      exit
    end

    res.entries.map do |batch_res_entry|
      # Need to check errors for each entry.
      if batch_res_entry.errors
        batch_res_entry.errors do |sub_err|
          handle_errors(sub_err)
        end
        puts
      else
        puts "Batch item #{batch_res_entry.batch_id} successful."
      end
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)

  unless ARGV.size >= 1
    puts "Usage: #{$0} PRODUCT_ID_1 [PRODUCT_ID_2 ...]"
    exit
  end
  product_ids = ARGV

  config, content_api = service_setup(options)
  delete_product_batch(content_api, config.merchant_id, product_ids)
end
