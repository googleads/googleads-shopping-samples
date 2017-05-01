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
# Adds several client accounts to the specified parent account, in a single
# batch.

require_relative 'mca_common'

BATCH_SIZE = 5

def insert_account_batch(content_api, merchant_id)
  requests = (1..BATCH_SIZE).map do |n|
    example_id = 'account%s' % unique_id()
    account = create_example_account(example_id)
    Google::Apis::ContentV2::AccountsBatchRequestEntry.new({
        :merchant_id => merchant_id,
        :account => account,
        :batch_id => n,
        :request_method => 'insert'})
  end

  batch_req =
      Google::Apis::ContentV2::BatchAccountsRequest.new({:entries => requests})

  content_api.batch_account(batch_req) do |res, err|
    if err
      puts "Overall batch call resulted in an error."
      handle_errors(err)
      exit
    end

    res.entries.each do |batch_resp|
      if batch_resp.errors
        puts "Batch item #{batch_resp.batch_id} resulted in an error."
        batch_resp.errors.each do |sub_err|
          handle_errors(sub_err)
        end
      else
        puts "Batch item #{batch_resp.batch_id} successful."
        puts "Created account #{batch_resp.account.id} for MCA #{merchant_id}"
      end
      puts
    end
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  unless config.is_mca
    puts "Merchant in configuration is not described as an MCA."
    exit
  end
  insert_account_batch(content_api, config.merchant_id)
end
