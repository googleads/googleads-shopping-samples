#!/usr/bin/ruby
# Encoding: utf-8
#
# Copyright:: Copyright 2017, Google Inc. All Rights Reserved.
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
# Contains the parser for command line arguments for the Shopping samples.

require 'optparse'
require 'ostruct'

class ArgParser

  def self.parse(args)
    options = OpenStruct.new
    options.path = File.join(Dir.home(), 'shopping-samples')
    options.noconfig = false

    opt_parser = OptionParser.new do |opts|
      opts.banner = "Usage: #{$0} [options]"

      opts.separator ''
      opts.separator 'Specific options:'

      opts.on('-p', '--config_path PATH',
          'Path for Shopping samples configuration') do |path|
        if not Dir.exists? path
          STDERR.puts "ERROR: Directory '#{path}' does not exist."
          raise OptionParser::InvalidArgument, path
        end
        options.path = path
      end

      opts.on('-n', '--noconfig',
          'Run samples without a configuration') do
        options.noconfig = true
      end
    end

    opt_parser.parse!(args)
    options
  end

end


