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
# Links the specified AdWords account to the specified merchant center account.

require_relative '../shopping_common'

def link_adwords_account(content_api, merchant_id, adwords_id)
  # First we need to retrieve the existing set of linked AdWords accounts.
  # We just want the list of linked AdWords accounts, not the entire object.
  account = content_api.get_account(
      merchant_id, merchant_id, fields: 'adwordsLinks'
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

  # Add new AdWords account to existing links.
  adwords_links = account.adwords_links || []
  adwords_link = Google::Apis::ContentV2::AccountAdwordsLink.new({
    :adwords_id => adwords_id,
    :status => 'active'})
  adwords_links << adwords_link
  account.adwords_links = adwords_links

  # Patch account with new list of AdWords links.
  response = content_api.patch_account(merchant_id, merchant_id, account) do
      |res, err|
    if err
      handle_errors(err)
      exit
    end

    puts "AdWords account %s linked to merchant center account %s." %
        [adwords_id, merchant_id]
  end
end


if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options)
  adwords_id = config.account_sample_adwords_cid
  if adwords_id.nil? or adwords_id != 0
    puts "No account sample AdWords CID in the configuration."
    exit
  end
  link_adwords_account(content_api, config.merchant_id, adwords_id)
end
