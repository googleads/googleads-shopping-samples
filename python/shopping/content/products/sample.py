#!/usr/bin/python
#
# Copyright 2016 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Creates a sample product object for the product samples."""
from shopping.content import _constants


def create_product_sample(config, offer_id, **overwrites):
  """Creates a sample product object for the product samples.

  Args:
      config: dictionary, Python version of config JSON
      offer_id: string, offer id for new product
      **overwrites: dictionary, a set of product attributes to overwrite

  Returns:
      A new product in dictionary form.
  """
  website_url = config.get('websiteUrl', 'http://my-book-shop.com')

  product = {
      'offerId':
          offer_id,
      'title':
          'A Tale of Two Cities',
      'description':
          'A classic novel about the French Revolution',
      'link':
          website_url + '/tale-of-two-cities.html',
      'imageLink':
          website_url + '/tale-of-two-cities.jpg',
      'contentLanguage':
          _constants.CONTENT_LANGUAGE,
      'targetCountry':
          _constants.TARGET_COUNTRY,
      'channel':
          _constants.CHANNEL,
      'availability':
          'in stock',
      'condition':
          'new',
      'googleProductCategory':
          'Media > Books',
      'gtin':
          '9780007350896',
      'price': {
          'value': '2.50',
          'currency': 'USD'
      },
      'shipping': [{
          'country': 'US',
          'service': 'Standard shipping',
          'price': {
              'value': '0.99',
              'currency': 'USD'
          }
      }],
      'shippingWeight': {
          'value': '200',
          'unit': 'grams'
      }
  }
  product.update(overwrites)
  return product
