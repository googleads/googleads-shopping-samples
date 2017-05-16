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
"""This utility file contains the code for printing out order information."""

from __future__ import print_function

import shopping_common


def print_order(order):
  """Prints out the given order with some human-readable indentation.

  Args:
    order: The Python representation of a Order resource.
  """
  print('Order %s:' % order['id'])
  print('- Status: %s' % order['status'])
  print('- Merchant: %s' % order['merchantId'])
  if not shopping_common.json_absent_or_false(order, 'merchantOrderId'):
    print('- Merchant order ID: %s' % order['merchantOrderId'])
  if not shopping_common.json_absent_or_false(order, 'customer'):
    print('- Customer information:')
    print('  - Full name: %s' % order['customer']['fullName'])
    print('  - Email: %s' % order['customer']['email'])
  print('- Placed on date: %s' % order['placedDate'])
  if not shopping_common.json_absent_or_false(order, 'netAmount'):
    print('- Name amount: %s %s' % (order['netAmount']['value'],
                                    order['netAmount']['currency']))
  print('- Payment status: %s' % order['paymentStatus'])
  if not shopping_common.json_absent_or_false(order, 'paymentMethod'):
    print('- Payment method:')
    print('  - Type: %s' % order['paymentMethod']['type'])
    print('  - Expiration date: %s/%s' %
          (order['paymentMethod']['expirationMonth'],
           order['paymentMethod']['expirationYear']))
  if shopping_common.json_absent_or_false(order, 'acknowledged'):
    print('- Acknowledged: no')
  else:
    print('- Acknowledged: yes')
  if not shopping_common.json_absent_or_false(order, 'lineItems'):
    print('- %d line item(s):' % len(order['lineItems']))
    for item in order['lineItems']:
      _print_line_item(item)
  print('- Shipping option: %s' % order['shippingOption'])
  if not shopping_common.json_absent_or_false(order, 'shippingCost'):
    print('- Shipping cost: %s %s' % (order['shippingCost']['value'],
                                      order['shippingCost']['currency']))
  if not shopping_common.json_absent_or_false(order, 'shippingCostTax'):
    print('- Shipping cost tax: %s %s' % (order['shippingCostTax']['value'],
                                          order['shippingCostTax']['currency']))
  if not shopping_common.json_absent_or_false(order, 'shipments'):
    print('- %d shipments(s):' % len(order['shipments']))
    for shipment in order['shipments']:
      print('  Shipment %s:' % shipment['id'])
      print('  - Creation date: %s:' % shipment['creationDate'])
      print('  - Carrier: %s:' % shipment['carrier'])
      print('  - Tracking ID: %s:' % shipment['trackingId'])
      if not shopping_common.json_absent_or_false(shipment, 'lineItems'):
        print('  - %d line item(s):' % len(shipment['lineItems']))
        for item in shipment['lineItems']:
          print('    %d of item %s' % (item['quantity'], item['lineItemId']))
      if not shopping_common.json_absent_or_false(shipment, 'deliveryDate'):
        print('  - Delivery date: %s' % shipment['deliveryDate'])


def _print_line_item(item):
  """Factored out line item printing to reduce nesting depth."""

  def print_if_nonzero(value, text):
    if value > 0:
      print('  - %s: %s' % (text, value))

  print('  Line item %s' % item['id'])
  print('  - Product: %s (%s)' % (item['product']['id'],
                                  item['product']['title']))
  print('  - Price: %s %s' % (item['price']['value'],
                              item['price']['currency']))
  print('  - Tax: %s %s' % (item['tax']['value'], item['tax']['currency']))
  print_if_nonzero(item['quantityOrdered'], 'Quantity ordered')
  print_if_nonzero(item['quantityPending'], 'Quantity pending')
  print_if_nonzero(item['quantityShipped'], 'Quantity shipped')
  print_if_nonzero(item['quantityDelivered'], 'Quantity delivered')
  print_if_nonzero(item['quantityReturned'], 'Quantity returned')
  print_if_nonzero(item['quantityCanceled'], 'Quantity canceled')
  if not shopping_common.json_absent_or_false(item, 'shippingDetails'):
    print('  - Ship by date: %s' % item['shippingDetails']['shipByDate'])
    print('  - Deliver by date: %s' % item['shippingDetails']['deliverByDate'])
    method = item['shippingDetails']['method']
    print('  - Deliver via %s %s (%s - %s days).' %
          (method['carrier'], method['methodName'], method['minDaysInTransit'],
           method['maxDaysInTransit']))
  if not shopping_common.json_absent_or_false(item, 'cancellations'):
    print('  - %d cancellation(s):' % len(item['cancellations']))
    for cancel in item['cancellations']:
      print('    Cancellation:')
      if not shopping_common.json_absent_or_false(cancel, 'actor'):
        print('    - Actor: %s' % cancel['actor'])
      print('    - Creation date: %s' % cancel['creationDate'])
      print('    - Quantity: %d' % cancel['quantity'])
      print('    - Reason: %s' % cancel['reason'])
      print('    - Reason text: %s' % cancel['reasonText'])
  if (not shopping_common.json_absent_or_false(item, 'returnInfo') and
      item['returnInfo']['isReturnable']):
    print('  - Item is returnable.')
    print('    - Days to return: %s' % item['returnInfo']['daysToReturn'])
    print('    - Return policy is at %s.' % item['returnInfo']['policyUrl'])
  else:
    print('  - Item is not returnable.')
  if not shopping_common.json_absent_or_false(item, 'returns'):
    print('  - %d return(s):' % len(item['returns']))
    for ret in item['returns']:
      print('    Return:')
      if not shopping_common.json_absent_or_false(ret, 'actor'):
        print('    - Actor: %s' % ret['actor'])
      print('    - Creation date: %s' % ret['creationDate'])
      print('    - Quantity: %d' % ret['quantity'])
      print('    - Reason: %s' % ret['reason'])
      print('    - Reason text: %s' % ret['reasonText'])
