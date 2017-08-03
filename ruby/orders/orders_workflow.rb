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
# Performs a simulated workflow for a single order on the sandbox API
# endpoint. We do this on the sandbox API endpoint for two reasons:
# 1) to have access to methods needed to create/advance test orders, and
# 2) to avoid accidental mutation of existing real orders.

require_relative 'orders_common'

def order_workflow(content_api, merchant_id)
  # Create a new test order using the template1 template. Normally orders would
  # be automatically populated by Google in the non-sandbox version, and we'd
  # skip ahead to find out what orders are currently waiting on us.
  print "Creating test order... "
  req = Google::Apis::ContentV2::OrdersCreateTestOrderRequest.new
  req.template_name = "template1"
  # For most of the calls in this workflow, we'll not check the return value
  # and instead use the simplified flow that just raises the error if it
  # happens and returns the response otherwise.
  resp = content_api.create_test_order(merchant_id, req)
  order_id = resp.order_id
  puts "done with id #{order_id}."
  puts

  # List all unacknowledged orders (a call like this is where we'd normally get
  # order_id values to operate on).
  puts "Listing unacknowledged orders for merchant #{merchant_id}:"
  list_all_orders(content_api, merchant_id, acknowledged: false)
  puts

  # Acknowledge the newly received order.
  print "Acknowledging order #{order_id}... "
  req = Google::Apis::ContentV2::OrdersAcknowledgeRequest.new
  req.operation_id = new_operation_id()
  resp = content_api.acknowledge_order(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  # Set the new order's merchant order ID.  We'll just use a random 32-bit int.
  merchant_order_id = "test order #{Random.rand(2**32 - 1)}"
  print "Updating merchant order ID to '#{merchant_order_id}'... "
  req = Google::Apis::ContentV2::OrdersUpdateMerchantOrderIdRequest.new
  req.merchant_order_id = merchant_order_id
  req.operation_id = new_operation_id()
  resp = content_api.update_merchant_order_id(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  print "Retrieving order with merchant order ID '#{merchant_order_id}'... "
  resp =
     content_api.get_order_by_merchant_order_id(merchant_id, merchant_order_id)
  print "done.\n\n"
  current_order = resp.order
  print_order(current_order)
  puts

  # Oops, not enough stock for all the Chromecasts ordered, so we cancel one
  # of them.
  print "Canceling one Chromecast order... "
  req = Google::Apis::ContentV2::OrdersCancelLineItemRequest.new
  req.operation_id = new_operation_id()
  req.line_item_id = current_order.line_items[0].id
  req.quantity = 1
  req.reason = "noInventory"
  req.reason_text = "Ran out of inventory while fulfilling request."
  resp = content_api.cancel_order_line_item(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # Advance the test order to the shippable state. Normally this would be done
  # by Google when an order is no longer cancelable by the customer, but here
  # we need to do it manually.
  print "Advancing test order... "
  content_api.advance_test_order(merchant_id, order_id)
  puts "done."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # To simulate partial fulfillment, we'll pick the first line item and ship
  # the still-pending amount.
  print "Notifying Google about shipment of first line item... "
  line_item = Google::Apis::ContentV2::OrderShipmentLineItemShipment.new
  line_item.line_item_id = current_order.line_items[0].id
  line_item.quantity = current_order.line_items[0].quantity_pending
  # Storing this in a non-reused variable so we can access the randomly
  # generated shipping/tracking IDs later, since we're not using actual
  # information from a product shipping database.
  shipment_req1 = Google::Apis::ContentV2::OrdersShipLineItemsRequest.new
  shipment_req1.operation_id = new_operation_id()
  shipment_req1.line_items = [ line_item ]
  shipment_req1.carrier =
      current_order.line_items[0].shipping_details.method_prop.carrier
  shipment_req1.shipment_id = "#{Random.rand(2**32 - 1)}"
  shipment_req1.tracking_id = "#{Random.rand(2**32 - 1)}"
  resp = content_api.shiplineitems_order(merchant_id, order_id, shipment_req1)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # Now we ship the rest.
  print "Notifying Google about shipment of second line item... "
  line_item = Google::Apis::ContentV2::OrderShipmentLineItemShipment.new
  line_item.line_item_id = current_order.line_items[1].id
  line_item.quantity = current_order.line_items[1].quantity_pending
  shipment_req2 = Google::Apis::ContentV2::OrdersShipLineItemsRequest.new
  shipment_req2.operation_id = new_operation_id()
  shipment_req2.line_items = [ line_item ]
  shipment_req2.carrier =
      current_order.line_items[1].shipping_details.method_prop.carrier
  shipment_req2.shipment_id = "#{Random.rand(2**32 - 1)}"
  shipment_req2.tracking_id = "#{Random.rand(2**32 - 1)}"
  resp = content_api.shiplineitems_order(merchant_id, order_id, shipment_req2)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # First item arrives to the customer.
  print "Notifying Google about delivery of first line item... "
  req = Google::Apis::ContentV2::OrdersUpdateShipmentRequest.new
  req.operation_id = new_operation_id()
  req.carrier = shipment_req1.carrier
  req.tracking_id = shipment_req1.tracking_id
  req.shipment_id = shipment_req1.shipment_id
  req.status = "delivered"
  resp = content_api.update_order_shipment(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # Second item arrives to the customer.
  print "Notifying Google about delivery of second line item... "
  req = Google::Apis::ContentV2::OrdersUpdateShipmentRequest.new
  req.operation_id = new_operation_id()
  req.carrier = shipment_req2.carrier
  req.tracking_id = shipment_req2.tracking_id
  req.shipment_id = shipment_req2.shipment_id
  req.status = "delivered"
  resp = content_api.update_order_shipment(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts

  # Customer returns one of first item since it was broken on arrival.
  print "Notifying Google about return of first line item... "
  req = Google::Apis::ContentV2::OrdersReturnLineItemRequest.new
  req.operation_id = new_operation_id()
  req.line_item_id = current_order.line_items[0].id
  req.quantity = 1
  req.reason = "productArrivedDamaged"
  req.reason_text = "Item broken upon receipt."
  resp = content_api.return_order_line_item(merchant_id, order_id, req)
  puts "done (#{resp.execution_status})."
  puts

  current_order = content_api.get_order(merchant_id, order_id)
  print_order(current_order)
  puts
end

# All (non-test) requests that change an order must have a unique operation
# ID over the lifetime of the order to enable Google to detect and reject
# duplicate requests.
# Here, we just use a nonce and bump it each time, since we're not retrying.
# This isn't thread-safe, but okay here as we're sending the requests
# sequentially.
@nonce = 0
def new_operation_id()
  operation_id = "#{@nonce}"
  @nonce += 1
  return operation_id
end

def list_all_orders(content_api, merchant_id, **args)
  content_api.list_orders(merchant_id, **args) do |res, err|
    if err
      handle_errors(err)
      exit
    end

    unless res.resources
      puts 'No results.'
      return
    end

    res.resources.each { |order| print_order(order) }

    return unless res.next_page_token
    args[:page_token] = res.next_page_token
    list_all_orders(content_api, merchant_id, **args)
  end

end

if __FILE__ == $0
  options = ArgParser.parse(ARGV)
  config, content_api = service_setup(options, use_sandbox = true)
  order_workflow(content_api, config.merchant_id)
end

