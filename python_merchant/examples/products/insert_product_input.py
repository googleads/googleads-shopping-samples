# -*- coding: utf-8 -*-
# Copyright 2024 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""A module to insert a Product Input."""

# [START InsertProductInput]
from examples.authentication import generate_user_credentials
from google.shopping import merchant_products_v1beta
from google.shopping.type import Channel
from google.shopping.type import Price

# ENSURE you fill in the merchant account and datasource ID for the sample to
# work.
_ACCOUNT = "[INSERT_ACCOUNT_HERE]"
_PARENT = f"accounts/{_ACCOUNT}"
# You can only insert products into datasource types of Input "API" and
# "FILE", and of Type "Primary" or "Supplemental."
_DATA_SOURCE = "[INSERT_DATA_SOURCE_HERE]"
_DATA_SOURCE_NAME = f"accounts/{_ACCOUNT}/dataSources/{_DATA_SOURCE}"


def create_product_input():
  """Creates a `ProductInput` resource."""

  # Creates a shipping setting
  price = Price()
  price.amount_micros = 33_450_000
  price.currency_code = "GBP"

  shipping_option_1 = merchant_products_v1beta.Shipping()
  shipping_option_1.price = price
  shipping_option_1.country = "GB"
  shipping_option_1.service = "1st class post"

  price2 = Price()
  price2.amount_micros = 33_450_000
  price2.currency_code = "EUR"

  shipping_option_2 = merchant_products_v1beta.Shipping()
  shipping_option_2.price = price2
  shipping_option_2.country = "FR"
  shipping_option_2.service = "2nd class post"

  # Sets product attributes.
  attributes = merchant_products_v1beta.Attributes()
  attributes.title = "A Tale of Two Cities"
  attributes.description = "A classic novel about the French Revolution"
  attributes.link = "https://exampleWebsite.com/tale-of-two-cities.html"
  attributes.image_link = "https://exampleWebsite.com/tale-of-two-cities.jpg"
  attributes.availability = "in stock"
  attributes.condition = "new"
  attributes.google_product_category = "Media > Books"
  attributes.gtin = "9780007350896"
  attributes.shipping = [shipping_option_1, shipping_option_2]

  return merchant_products_v1beta.ProductInput(
      channel=Channel.ChannelEnum.ONLINE,
      content_language="en",
      feed_label="GB",
      offer_id="sku123",
      attributes=attributes,
  )


def insert_product_input():
  """Inserts the specified `ProductInput` resource."""

  # Gets OAuth Credentials.
  credentials = generate_user_credentials.main()

  # Creates a client.
  client = merchant_products_v1beta.ProductInputsServiceClient(
      credentials=credentials
  )

  # Creates the request.
  request = merchant_products_v1beta.InsertProductInputRequest(
      parent=_PARENT,
      # If this product is already owned by another datasource, when
      # re-inserting, the new datasource will take ownership of the product.
      product_input=create_product_input(),
      data_source=_DATA_SOURCE_NAME,
  )

  # Makes the request and catches and prints any error messages.
  try:
    response = client.insert_product_input(request=request)
    # The last part of the product name will be the product ID assigned to a
    # product by Google. Product ID has the format
    # `channel~contentLanguage~feedLabel~offerId`
    print(f"Input successful: {response}")
  except RuntimeError as e:
    print("Input failed")
    print(e)


# [END InsertProductInput]

if __name__ == "__main__":
  insert_product_input()
