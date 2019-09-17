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
"""This example runs through an example order workflow.

Unlike the other samples, this example contains an entire example workflow
using a test order.  In addition, this example uses the sandbox API endpoint
instead of the normal endpoint for two reasons:
1) It provides access to the methods for creating and operating on test orders.
2) It avoids accidentally mutating existing real orders.
"""

from __future__ import print_function
import random
import sys

from shopping.content import common
from shopping.content.orders import utils


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__, sandbox=True)
  orders = service.orders()
  merchant_id = config['merchantId']

  # Create a new test order using the template1 template. Normally, orders
  # would be automatically populated by Google in the non-sandbox version,
  # and we'd skip ahead to find out what orders are currently waiting for us.
  print('Creating new test order... ', end='')
  request = orders.createtestorder(
      merchantId=merchant_id, body={'templateName': 'template1'})
  response = request.execute()
  order_id = response['orderId']
  print('done (%s).\n' % order_id)

  # List all unacknowledged orders.  A call like this is where we'd normally
  # get new order_id values to operate on.
  print('Listing unacknowledged orders for merchant %d:' % merchant_id)
  _list_all_orders(orders, merchant_id, acknowledged=False)
  print()

  # Acknowledge the newly received order.
  print('Acknowledging order %s... ' % order_id, end='')
  request = orders.acknowledge(
      merchantId=merchant_id,
      orderId=order_id,
      body={'operationId': _new_operation_id()})
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  # Set the new order's merchant order ID. For here, we'll just use a
  # random int of 32 bits.
  merchant_order_id = 'test order %d' % random.getrandbits(32)
  print('Updating merchant order ID to "%s"... ' % merchant_order_id, end='')
  request = orders.updatemerchantorderid(
      merchantId=merchant_id,
      orderId=order_id,
      body={
          'operationId': _new_operation_id(),
          'merchantOrderId': merchant_order_id
      })
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  print('Retrieving merchant order "%s"... ' % merchant_order_id, end='')
  request = orders.getbymerchantorderid(
      merchantId=merchant_id, merchantOrderId=merchant_order_id)
  current_order = request.execute()['order']
  print('done.\n')
  utils.print_order(current_order)
  print()

  # Oops, not enough stock for all the Chromecasts ordered, so we cancel
  # one of them.
  print('Canceling one Chromecast order... ', end='')
  request = orders.cancellineitem(
      merchantId=merchant_id,
      orderId=order_id,
      body={
          'operationId': _new_operation_id(),
          'lineItemId': current_order['lineItems'][0]['id'],
          'quantity': 1,
          'reason': 'noInventory',
          'reasonText': 'Ran out of inventory while fulfilling request.'
      })
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # Advance the test order to the shippable state. Normally this would be done
  # by Google when an order is no longer cancelable by the customer, but here
  # we need to do it manually.
  print('Advancing test order... ', end='')
  orders.advancetestorder(merchantId=merchant_id, orderId=order_id).execute()
  print('done.\n')

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # To simulate partial fulfillment, we'll pick the first line item and
  # ship the still-pending amount.
  print('Notifying Google about shipment of first line item... ', end='')
  # Storing the request body so we can access the randomly generated
  # shipping/tracking IDs later. Normally we'd just look them
  # up in information we'd store about each shipment.
  item1 = current_order['lineItems'][0]
  shipping_request_1 = {
      'lineItems': [{
          'lineItemId': item1['id'],
          'quantity': item1['quantityPending']
      }],
      'shipmentInfos': [{
          'carrier': item1['shippingDetails']['method']['carrier'],
          'shipmentId': '%d' % random.getrandbits(32),
          'trackingId': '%d' % random.getrandbits(32)
      }],
      'operationId': _new_operation_id()
  }
  request = orders.shiplineitems(
      merchantId=merchant_id, orderId=order_id, body=shipping_request_1)
  response = request.execute()
  print('done (%s).' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # Now we ship the rest.
  print('Notifying Google about shipment of second line item... ', end='')
  item2 = current_order['lineItems'][1]
  shipping_request_2 = {
      'lineItems': [{
          'lineItemId': item2['id'],
          'quantity': item2['quantityPending']
      }],
      'shipmentInfos': [{
          'carrier': item2['shippingDetails']['method']['carrier'],
          'shipmentId': '%d' % random.getrandbits(32),
          'trackingId': '%d' % random.getrandbits(32)
      }],
      'operationId': _new_operation_id()
  }
  request = orders.shiplineitems(
      merchantId=merchant_id, orderId=order_id, body=shipping_request_2)
  response = request.execute()
  print('done (%s).' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # Customer receives the first item.
  print('Notifying Google about delivery of first line item... ', end='')
  request = orders.updateshipment(
      merchantId=merchant_id,
      orderId=order_id,
      body={
          'shipmentId': shipping_request_1['shipmentInfos'][0]['shipmentId'],
          'trackingId': shipping_request_1['shipmentInfos'][0]['trackingId'],
          'carrier': shipping_request_1['shipmentInfos'][0]['carrier'],
          'status': 'delivered',
          'operationId': _new_operation_id()
      })
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # Customer receives the second item.
  print('Notifying Google about delivery of second line item... ', end='')
  request = orders.updateshipment(
      merchantId=merchant_id,
      orderId=order_id,
      body={
          'shipmentId': shipping_request_2['shipmentInfos'][0]['shipmentId'],
          'trackingId': shipping_request_2['shipmentInfos'][0]['trackingId'],
          'carrier': shipping_request_2['shipmentInfos'][0]['carrier'],
          'status': 'delivered',
          'operationId': _new_operation_id()
      })
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()

  # Customer returns one of the first item due to being broken on delivery.
  print('Notifying Google about return of first line item... ', end='')
  request = orders.returnrefundlineitem(
      merchantId=merchant_id,
      orderId=order_id,
      body={
          'lineItemId': item1['id'],
          'quantity': 1,
          'reason': 'productArrivedDamaged',
          'reasonText': 'Item broken at receipt.',
          'operationId': _new_operation_id()
      })
  response = request.execute()
  print('done (%s).\n' % response['executionStatus'])

  request = orders.get(merchantId=merchant_id, orderId=order_id)
  current_order = request.execute()
  utils.print_order(current_order)
  print()


_nonce = 0


# All (non-test) requests that change an order must have a unique operation
# ID over the lifetime of the order to enable Google to detect and reject
# duplicate requests.
# Here, we just use a nonce and bump it each time, since we're not retrying.
def _new_operation_id():
  global _nonce
  operation_id = str(_nonce)
  _nonce += 1
  return operation_id


# List all the orders in an account. Can be passed args that are understood
# by service.orders().list(), such as filtering by acknowledgment.
def _list_all_orders(orders, merchant_id, **args):
  request = orders.list(merchantId=merchant_id, **args)
  while request is not None:
    result = request.execute()
    order_resources = result.get('resources')
    if not order_resources:
      print('No orders were found.')
      break
    for order in order_resources:
      utils.print_order(order)
    request = orders.list_next(request, result)


if __name__ == '__main__':
  main(sys.argv)
