#!/usr/bin/ruby
#
# Tests the sample shipping settings builder for obvious errors by building
# a sample shipping settings.

require "test/unit"
require_relative "../shipping_settings/shipping_settings_common.rb"

class TestShippingSettingsSample < Test::Unit::TestCase
  def test_creation
    assert_kind_of(Google::Apis::ContentV2_1::ShippingSettings,
        create_sample_shipping_settings)
  end
end
