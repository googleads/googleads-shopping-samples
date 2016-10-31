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
# Handles loading the common Google Content API for Shopping sample
# configuration JSON file.  Also allows for writing the current emailAddress
# and token back to the configuration file.

require 'multi_json'

MultiJson.dump_options = { pretty: true }

class Config
  class << self
    attr_accessor :path
    attr_accessor :file

    def load()
      new(MultiJson.load(IO.read(@file)))
    end
  end

  attr_accessor :merchant_id
  attr_accessor :application_name
  attr_accessor :email_address
  attr_accessor :is_mca
  attr_accessor :website_url
  attr_accessor :account_sample_user
  attr_accessor :account_sample_adwords_cid
  attr_accessor :token

  def initialize(options)
    @merchant_id = options["merchantId"]
    @application_name = options["applicationName"]
    @email_address = options["emailAddress"]
    @is_mca = options["isMCA"]
    @website_url = options["websiteUrl"]
    @account_sample_user = options["accountSampleUser"]
    @account_sample_adwords_cid = options["accountSampleAdWordsCID"]
    @token = options["token"]
  end

  def write()
    output = File.open(Config.file, "w")
    output << MultiJson.dump(
      merchantId: @merchant_id,
      applicationName: @application_name,
      emailAddress: @email_address,
      isMCA: @is_mca,
      websiteUrl: @website_url,
      accountSampleUser: @account_sample_user,
      accountSampleAdWordsCID: @account_sample_adwords_cid,
      token: @token
    )
    output.close
  end
end

Config.path = File.join(Dir.home(), '.shopping-content-samples')
Config.file = File.join(Config.path, 'merchant-info.json')
