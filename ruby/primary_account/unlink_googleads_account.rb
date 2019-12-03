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
# Unlinks the specified Google Ads account from the specified merchant center
# account.

require_relative "../shopping_common"

def unlink_googleads_account(content_api, merchant_id, googleads_id)
  # First we need to retrieve the existing set of linked Google Ads accounts.
  # We just want the list of linked Google Ads accounts, not the entire object.
  account = content_api.get_account(
    merchant_id, merchant_id
  ) do |res, err|
    if err
      if err.status == 404
        puts "Account #{merchant_id} not found."
      else
        handle_errors(err)
      end
      exit
    end
  end

  ads_links = account.ads_links || []

  # Remove account from existing links.
  ads_links.reject! do |ads_link|
    ads_link.ads_id.to_s == googleads_id.to_s
  end

  account.ads_links = ads_links

  # Update account with new list of Google Ads links.
  response = content_api.update_account(merchant_id, merchant_id, account) do
      |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts "Google Ads account #{googleads_id} unlinked from merchant center account #{merchant_id}."
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  googleads_id = config.account_sample_googleads_cid
  if googleads_id.nil? || googleads_id == 0
    puts "No account sample Google Ads CID in the configuration."
    exit
  end
  unlink_googleads_account(content_api, config.merchant_id, googleads_id)
end
