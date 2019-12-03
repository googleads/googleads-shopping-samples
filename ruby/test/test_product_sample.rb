#!/usr/bin/ruby
#
# Tests the sample product builder for obvious errors by building
# a sample product.

require "test/unit"
require_relative "../product/product_common.rb"

class TestProductSample < Test::Unit::TestCase
  class SampleConfig
    attr_accessor :website_url
    def initialize(url)
      @website_url = url
    end
  end

  def test_creation
    config = SampleConfig.new("http://testing.google.com")
    assert_kind_of(Google::Apis::ContentV2_1::Product,
        create_example_product(config, "test"))
  end
end
