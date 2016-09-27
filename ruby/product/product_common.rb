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
# Handles common tasks across all product samples.

require_relative '../shopping_common'

# Creates an example product with the provided offer ID.
def create_example_product(offer_id)
  return Google::Apis::ContentV2::Product.new({
    :offer_id => offer_id,
    :title => 'A Tale of Two Cities',
    :description => 'A classic novel about the French Revolution',
    :link => 'http://my-book-shop.com/tale-of-two-cities.html',
    :image_link => 'http://my-book-shop.com/tale-of-two-cities.jpg',
    :content_language => CONTENT_LANGUAGE,
    :target_country => TARGET_COUNTRY,
    :channel => CHANNEL,
    :availability => 'in stock',
    :condition => 'new',
    :google_product_category => 'Media > Books',
    :gtin => '9780007350896',
    :price => Google::Apis::ContentV2::Price({
        :value => '2.50',
        :currency => 'USD'}),
    :shipping => Google::Apis::ContentV2::ProductShipping({
        :country => 'US',
        :service => 'Standard shipping',
        :price => Google::Apis::ContentV2::Price({
            :value => '0.99',
            :currency => 'USD'})}),
    shipping_weight: Google::Apis::ContentV2::ProductShippingWeight({
        :value => '200',
        :unit => 'grams'})})
end
