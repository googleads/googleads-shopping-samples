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

class ContentConfig
  def self.load(base_path)
    path = File.join(base_path, 'content')
    unless File.exist?(path)
      raise "Content API configuration path does not exist: #{path}"
    end
    file = File.join(path, 'merchant-info.json')
    if File.exist?(file)
      new(path, file, MultiJson.load(IO.read(file)))
    else
      puts "No configuration file found at #{file}."
      puts "Assuming configuration defaults for authenticated user."
      new(path)
    end
  end

  attr_accessor :path
  attr_accessor :merchant_id
  attr_accessor :account_sample_user
  attr_accessor :account_sample_adwords_cid

  # is_mca and website_url are no longer read from the config file, but
  # via API calls, so it cannot be reliably used until after service
  # initialization. Write our own accessor to check for uninitalized fields
  # and fail if they are accessed before service_setup() is run.
  attr_writer :is_mca
  attr_writer :website_url

  def is_mca()
    if @is_mca.nil?
      puts "Attempted to use is_mca field of config before initialization."
      puts "Please see config.rb for more information."
      raise "Use of is_mca config field before completing service_setup"
    end
    return @is_mca
  end

  def website_url()
    if @website_url.nil?
      puts "Attempted to use website_url field of config before initialization."
      puts "Please see config.rb for more information."
      raise "Use of website_url config field before completing service_setup"
    end
    return @website_url
  end

  def initialize(path = nil, file = nil, options = nil)
    @path = path
    @file = file
    unless options.nil?
      @merchant_id = options["merchantId"]
      @account_sample_user = options["accountSampleUser"]
      @account_sample_adwords_cid = options["accountSampleAdWordsCID"]
    end
  end
end
