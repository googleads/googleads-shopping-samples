#!/usr/bin/ruby
#
# Tests the sample account builder for obvious errors by building
# a sample product.

require "test/unit"
require_relative "../mca/mca_common.rb"

class TestAccountSample < Test::Unit::TestCase
  def test_creation
    assert_kind_of(Google::Apis::ContentV2_1::Account,
        create_example_account("test"))
  end
end
