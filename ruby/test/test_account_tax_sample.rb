#!/usr/bin/ruby
#
# Tests the sample tax settings builder for obvious errors by building
# a sample tax settings.

require "test/unit"
require_relative "../account_tax/account_tax_common.rb"

class TestAccountTaxSample < Test::Unit::TestCase
  def test_creation
    assert_kind_of(Google::Apis::ContentV2_1::AccountTax,
        create_sample_account_tax(0))
  end
end
