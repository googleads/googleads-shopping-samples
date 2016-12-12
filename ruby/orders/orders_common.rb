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
# Handles common tasks across all orders samples.

require_relative '../shopping_common'

# Prints out the given order with some human-readable indentation.
def print_order(order)
  puts "Order #{order.id}:"
  puts "- Status: #{order.status}"
  puts "- Merchant: #{order.merchant_id}"
  puts "- Merchant order ID: #{order.merchant_order_id}"
  if order.customer
    puts "- Customer information:"
    puts "  - Full name: #{order.customer.full_name}"
    puts "  - Email: #{order.customer.email}"
  end
  puts "- Placed on date: #{order.placed_date}"
  if order.net_amount
    puts "- Net amount: #{order.net_amount.value} #{order.net_amount.currency}"
  end
  puts "- Payment status: #{order.payment_status}"
  if order.payment_method
    puts "- Payment method:"
    puts "  - Type: #{order.payment_method.type}"
    exp_month = order.payment_method.expiration_month
    exp_year = order.payment_method.expiration_year
    puts "  - Expiration date: #{exp_month}/#{exp_year}"
  end
  puts "- Acknowledged: #{order.acknowledged? ? "yes" : "no" }"
  if order.line_items and order.line_items.length > 0
    puts "- #{order.line_items.length} line item(s):"
    order.line_items.each { |item| print_line_item(item) }
  end
  puts "- Shipping option: #{order.shipping_option}"
  if order.shipping_cost
    cost = order.shipping_cost
    puts "- Shipping cost: #{cost.value} #{cost.currency}"
  end
  if order.shipping_cost_tax
    tax = order.shipping_cost_tax
    puts "- Shipping cost tax: #{tax.value} #{tax.currency}"
  end
  if order.shipments and order.shipments.length > 0
    puts "- #{order.shipments.length} shipment(s):"
    order.shipments.each do |shipment|
      puts "  Shipment #{shipment.id}"
      puts "  - Creation date: #{shipment.creation_date}"
      puts "  - Carrier: #{shipment.carrier}"
      puts "  - Tracking ID: #{shipment.tracking_id}"
      if shipment.line_items and shipment.line_items.length > 0
        puts "  - #{shipment.line_items.length} line item(s):"
        shipment.line_items.each do |item|
          puts "    #{item.quantity} of item #{item.line_item_id}"
        end
      end
      if shipment.delivery_date
        puts "  - Delivery date: #{shipment.delivery_date}"
      end
    end
  end
end

def print_if_nonzero(value, text, indent = "")
     puts "#{indent}- #{text}: #{value}" if value > 0
end

def print_line_item(item)
  puts "  Line item #{item.id}:"
  puts "  - Product: #{item.product.id} (#{item.product.title})"
  puts "  - Price: #{item.price.value} #{item.price.currency}"
  puts "  - Tax: #{item.tax.value} #{item.tax.currency}"
  print_if_nonzero(item.quantity_ordered, "Quantity ordered", "  ")
  print_if_nonzero(item.quantity_pending, "Quantity pending", "  ")
  print_if_nonzero(item.quantity_shipped, "Quantity shipped", "  ")
  print_if_nonzero(item.quantity_delivered, "Quantity delivered", "  ")
  print_if_nonzero(item.quantity_returned, "Quantity returned", "  ")
  print_if_nonzero(item.quantity_canceled, "Quantity canceled", "  ")
  if item.shipping_details
    puts "  - Ship by date: #{item.shipping_details.ship_by_date}"
    puts "  - Deliver by date: #{item.shipping_details.deliver_by_date}"
    method = item.shipping_details.method_prop
    print "  - Deliver via #{method.carrier} #{method.method_name} "
    puts "(#{method.min_days_in_transit} - #{method.max_days_in_transit} days)"
  end
  if item.return_info and item.return_info.is_returnable?
    puts "  - Item is returnable."
    puts "    - Days to return: #{item.return_info.days_to_return}"
    puts "    - Return policy is at #{item.return_info.policy_url}."
  else
    puts "  - Item is not returnable."
  end
  if item.returns and item.returns.length > 0
    puts "  - #{item.returns.length} return(s):"
    item.returns.each do |ret|
      puts "    Return:"
      puts "    - Actor: #{ret.actor}" if ret.actor
      puts "    - Creation date: #{ret.creation_date}"
      puts "    - Quantity: #{ret.quantity}"
      puts "    - Reason: #{ret.reason}"
      puts "    - Reason text: #{ret.reason_text}"
    end
  end
end
