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
# Adds several products to the specified account, in a single batch.

require_relative 'product_common'

BATCH_SIZE = 5

def insert_product_batch(content_api, config)
  requests = (1..BATCH_SIZE).map do |n|
    example_id = 'book#%s' % unique_id()
    product = create_example_product(config, example_id)
    Google::Apis::ContentV2::ProductsBatchRequestEntry.new({
      :batch_id => n,
      :merchant_id => config.merchant_id,
      :product => product,
      :request_method => 'insert'})
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
        puts "Product created with ID #{batch_res_entry.product.id}"

        handle_warnings(batch_res_entry.product)
        puts
      end
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  insert_product_batch(content_api, config)
end
