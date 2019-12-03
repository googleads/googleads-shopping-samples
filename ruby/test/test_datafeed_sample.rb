#!/usr/bin/ruby
#
# Tests the sample datafeed builder for obvious errors by building
# a sample datafeed.

require "test/unit"
require_relative "../datafeed/datafeed_common.rb"

class TestDatafeedSample < Test::Unit::TestCase
  def test_creation
    assert_kind_of(Google::Apis::ContentV2_1::Datafeed,
        create_example_datafeed("test"))
  end
end
