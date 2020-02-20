using System;
using Google.Apis.ShoppingContent.v2_1;
using Google.Apis.Services;
using Google.Apis.ShoppingContent.v2_1.Data;
using System.Collections.Generic;
using CommandLine;

namespace ShoppingSamples.Content
{
    /// <summary>
    /// A sample consumer that runs an example workflow for a single test order using the Orders
    /// service in the Content API for Shopping.
    ///
    /// <para>Since access to the Orders service is limited, this sample is written as a separate
    /// program from the rest. Also unlike the other samples, this sample uses the sandbox API
    /// endpoint instead of the normal endpoint for two reasons:</para>
    /// <list type="bullet">
    /// <item>
    /// <description>It provides access to the methods for creating and operating on test
    /// orders.</description>
    /// </item>
    /// <item>
    /// <description>It avoids accidentally mutating existing real orders.</description>
    /// </item></list>
    /// </summary>
    internal class OrdersSample : BaseContentSample
    {
        private Random prng; // Used for random order/tracking/shipment ID creation.
        private ulong nonce; // Nonce used for creating new operation IDs.

        public OrdersSample()
        {
            this.prng = new Random();
        }

        internal override void runCalls()
        {
            var merchantId = config.MerchantId.Value;

            // First, we create a new test order using the template1 template.  Normally, orders
            // would be automatically populated by Google in the non-sandbox version, and we'd
            // skip ahead to find out what orders are currently waiting for our acknowledgment.
            Console.WriteLine("=================================================================");
            Console.WriteLine("Creating Test Order for {0}", merchantId);
            Console.WriteLine("=================================================================");

            string orderId;
            {
                var req = new OrdersCreateTestOrderRequest() {
                  TemplateName = "template1"
                };
                var resp = sandboxService.Orders.Createtestorder(req, merchantId).Execute();
                orderId = resp.OrderId;
            }

            Console.WriteLine("Order created with ID {0}", orderId);
            Console.WriteLine();

            // List all unacknowledged orders.  In normal usage, this is where we'd get new
            // order IDs to operate on.  We should see the new test order show up here.
            //ListAllUnacknowledgedOrders(merchantId);

            // Acknowledge the newly created order.  The assumption in doing so is that we've
            // collected the information for this order into our own internal system, and
            // acknowledging it allows us to skip it when searching for new orders (see
            // ListAllUnacknowledgedOrders()).
            Acknowledge(merchantId, orderId);

            // We'll use random numbers for a few things that would normally be supplied by
            // various systems, like the order IDs for the merchant's internal systems, the
            // shipping IDs for the merchant's internal systems, and the tracking ID we'd get
            // from the shipping carriers.
            string merchantOrderId = prng.Next().ToString();
            UpdateMerchantOrderId(merchantId, orderId, merchantOrderId);

            // Print out the current status of the order (and store a reference to the resource
            // so we can pull stuff out of it shortly).
            var currentOrder = GetOrderByMerchantOrderId(merchantId, merchantOrderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // Oops, not enough stock for all the Chromecasts ordered, so we cancel one of them.
            {
                var req = new OrdersCancelLineItemRequest() {
                  LineItemId = currentOrder.LineItems[0].Id,
                  Quantity = 1,
                  Reason = "noInventory",
                  ReasonText = "Ran out of inventory while fulfilling request.",
                  OperationId = NewOperationId()
                };

                CancelLineItem(merchantId, orderId, req);
            }

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // At this point we'll advance the test order, which simulates the point at which
            // an order is no longer cancelable by the customer and the order information reflects
            // that the order is ready for shipping.
            Console.WriteLine("=================================================================");
            Console.WriteLine("Advancing Test Order {0}", orderId);
            Console.WriteLine("=================================================================");
            sandboxService.Orders.Advancetestorder(merchantId, orderId).Execute();
            Console.WriteLine();

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // To simulate partial fulfillment, we'll pick the first line item and ship the
            // still-pending amount. (Remember, we cancelled one already.)
            var shipItem1Req = ShipAllLineItem(merchantId, orderId, currentOrder.LineItems[0]);

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // Now we ship the rest.
            var shipItem2Req = ShipAllLineItem(merchantId, orderId, currentOrder.LineItems[1]);

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // Customer receives the first item, so we notify Google that it was delivered.
            LineItemDelivered(merchantId, orderId, shipItem1Req);

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // Customer receives second item.
            LineItemDelivered(merchantId, orderId, shipItem2Req);

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();

            // Turns out one of the first items was broken when the customer received it, so
            // they returned it.  Let's make sure Google knows about it and why.
            {
                var req = new OrdersReturnRefundLineItemRequest();
                req.LineItemId = currentOrder.LineItems[0].Id;
                req.Quantity = 1;
                req.Reason = "productArrivedDamaged";
                req.ReasonText = "Item broken at receipt.";
                req.OperationId = NewOperationId();

                LineItemReturned(merchantId, orderId, req);
            }

            currentOrder = GetOrder(merchantId, orderId);
            PrintOrder(currentOrder);
            Console.WriteLine();
        }

        /// <summary>
        /// Retrieves a particular order given the (Google-specified) order ID.
        /// </summary>
        /// <returns>The order resource for the specified order.</returns>
        private Order GetOrder(ulong merchantId, string orderId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Order {0}", orderId);
            Console.WriteLine("=================================================================");


            Order status = sandboxService.Orders.Get(merchantId, orderId).Execute();
            Console.WriteLine();

            return status;
        }

        /// <summary>
        /// Retrieves a particular order given the merchant-specified order ID.
        /// </summary>
        /// <returns>The order resource for the specified order.</returns>
        private Order GetOrderByMerchantOrderId(ulong merchantId, string merchantOrderId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Getting Merchant Order {0}", merchantOrderId);
            Console.WriteLine("=================================================================");

            var resp =
                sandboxService.Orders.Getbymerchantorderid(merchantId, merchantOrderId).Execute();
            Console.WriteLine();

            return resp.Order;
        }

        /// <summary>
        /// Lists all the unacknowledged orders for the given merchant.
        /// </summary>
        private void ListAllUnacknowledgedOrders(ulong merchantId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Listing Unacknowledged Orders for Merchant {0}", merchantId);
            Console.WriteLine("=================================================================");

            // Retrieve orders list in pages and display data as we receive it.
            string pageToken = null;
            OrdersListResponse ordersResponse = null;

            do
            {
                OrdersResource.ListRequest ordersRequest = sandboxService.Orders.List(merchantId);
                ordersRequest.Acknowledged = false;
                ordersRequest.PageToken = pageToken;

                ordersResponse = ordersRequest.Execute();

                if (ordersResponse.Resources != null && ordersResponse.Resources.Count != 0)
                {
                    foreach (var order in ordersResponse.Resources)
                    {
                        PrintOrder(order);
                    }
                }
                else
                {
                    Console.WriteLine("No orders found.");
                }

                pageToken = ordersResponse.NextPageToken;
            } while (pageToken != null);
            Console.WriteLine();
        }

        // All (non-test) requests that change an order must have a unique operation
        // ID over the lifetime of the order to enable Google to detect and reject
        // duplicate requests.
        // Here, we just use a nonce and bump it each time, since we're not retrying failures
        // and sending them all sequentially.
        private string NewOperationId() {
            string str = nonce.ToString();
            nonce++;
            return str;
        }

        private void Acknowledge(ulong merchantId, string orderId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Acknowledging Order {0}", orderId);
            Console.WriteLine("=================================================================");

            var req = new OrdersAcknowledgeRequest() {
              OperationId = NewOperationId()
            };
            var resp = sandboxService.Orders.Acknowledge(req, merchantId, orderId).Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();
        }

        private void UpdateMerchantOrderId(ulong merchantId, string orderId, string merchantOrderId)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Updating Merchant Order ID to {0}", merchantOrderId);
            Console.WriteLine("=================================================================");

            var req = new OrdersUpdateMerchantOrderIdRequest() {
              OperationId = NewOperationId(),
              MerchantOrderId = merchantOrderId
            };
            var resp = sandboxService.Orders
                .Updatemerchantorderid(req, merchantId, orderId)
                .Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();
        }

        private void CancelLineItem(ulong merchantId, string orderId,
            OrdersCancelLineItemRequest req)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Canceling {0} of item {1}", req.Quantity, req.LineItemId);
            Console.WriteLine("=================================================================");

            var resp = sandboxService.Orders.Cancellineitem(req, merchantId, orderId).Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();
        }

        private OrdersShipLineItemsRequest ShipAllLineItem(ulong merchantId, string orderId,
            OrderLineItem item)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Shipping {0} of item {1}", item.QuantityPending, item.Id);
            Console.WriteLine("=================================================================");

            var itemShip = new OrderShipmentLineItemShipment() {
              LineItemId = item.Id,
              Quantity = item.QuantityPending
            };

            var shipmentInfo = new OrdersCustomBatchRequestEntryShipLineItemsShipmentInfo() {
              Carrier = item.ShippingDetails.Method.Carrier,
              ShipmentId = prng.Next().ToString(),
              TrackingId = prng.Next().ToString(),
            };

            var req = new OrdersShipLineItemsRequest {
              ShipmentInfos = new List<OrdersCustomBatchRequestEntryShipLineItemsShipmentInfo> { shipmentInfo },
              LineItems = new List<OrderShipmentLineItemShipment> { itemShip },
              OperationId = NewOperationId()
            };

            var resp = sandboxService.Orders.Shiplineitems(req, merchantId, orderId).Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();

            // We return req here so that we have access to the randomly-generated IDs in the
            // main program.
            return req;
        }

        private void LineItemDelivered(ulong merchantId, string orderId,
            OrdersShipLineItemsRequest ship)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Delivered {0} of item {1}", ship.LineItems[0].Quantity,
                ship.LineItems[0].LineItemId);
            Console.WriteLine("=================================================================");

            var req = new OrdersUpdateShipmentRequest() {
              Carrier = ship.ShipmentInfos[0].Carrier,
              TrackingId = ship.ShipmentInfos[0].TrackingId,
              ShipmentId = ship.ShipmentInfos[0].ShipmentId,
              Status = "delivered",
              OperationId = NewOperationId()
            };

            var resp = sandboxService.Orders.Updateshipment(req, merchantId, orderId).Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();
        }

        private void LineItemReturned(ulong merchantId, string orderId,
            OrdersReturnRefundLineItemRequest req)
        {
            Console.WriteLine("=================================================================");
            Console.WriteLine("Returned {0} of item {1}", req.Quantity,
                req.LineItemId);
            Console.WriteLine("=================================================================");

            var resp = sandboxService.Orders.Returnrefundlineitem(req, merchantId, orderId).Execute();

            Console.WriteLine("Finished with status {0}.", resp.ExecutionStatus);
            Console.WriteLine();
        }

        private void PrintOrder(Order order)
        {
            Console.WriteLine("Order {0}:", order.Id);
            Console.WriteLine("- Status: {0}", order.Status);
            Console.WriteLine("- Merchant: {0}", order.MerchantId);
            Console.WriteLine("- Merchant order ID: {0}", order.MerchantOrderId);
            if (order.Customer != null)
            {
                Console.WriteLine("- Customer information:");
                Console.WriteLine("  - Full name: {0}", order.Customer.FullName);
                if (order.Customer.MarketingRightsInfo != null)
                {
                    Console.WriteLine("  - Email: {0}", order.Customer.MarketingRightsInfo.MarketingEmailAddress);
                }
            }
            Console.WriteLine("- Placed on date: {0}", order.PlacedDate);
            if (order.NetPriceAmount != null)
            {
                Console.WriteLine("- Net amount: {0} {1}", order.NetPriceAmount.Value,
                    order.NetPriceAmount.Currency);
            }
            Console.WriteLine("- Payment status: {0}", order.PaymentStatus);
            Console.WriteLine("- Acknowledged: {0}", order.Acknowledged == true ? "yes" : "no");
            if (order.LineItems != null && order.LineItems.Count > 0)
            {
                Console.WriteLine("- {0} line item(s):", order.LineItems.Count);
                foreach (var item in order.LineItems)
                {
                    PrintOrderLineItem(item);
                }
            }
            if (order.ShippingCost != null)
            {
                Console.WriteLine("- Shipping cost: {0} {1}", order.ShippingCost.Value,
                    order.ShippingCost.Currency);
            }
            if (order.ShippingCostTax != null)
            {
                Console.WriteLine("- Shipping cost tax: {0} {1}", order.ShippingCostTax.Value,
                    order.ShippingCostTax.Currency);
            }
            if (order.Shipments != null && order.Shipments.Count > 0)
            {
                Console.WriteLine("- {0} shipment(s):", order.Shipments.Count);
                foreach (var shipment in order.Shipments)
                {
                    Console.WriteLine("  Shipment {0}", shipment.Id);
                    Console.WriteLine("  - Creation date: {0}", shipment.CreationDate);
                    Console.WriteLine("  - Carrier: {0}", shipment.Carrier);
                    Console.WriteLine("  - Tracking ID: {0}", shipment.TrackingId);
                    if (shipment.LineItems != null && shipment.LineItems.Count > 0)
                    {
                        Console.WriteLine("- {0} line item(s):", shipment.LineItems.Count);
                        foreach (var item in shipment.LineItems) {
                            Console.WriteLine("  {0} of item {1}", item.Quantity, item.LineItemId);
                        }
                    }
                    if (shipment.DeliveryDate != null) {
                        Console.WriteLine("  - Delivery date: {0}", shipment.DeliveryDate);
                    }
                }
            }
        }

        private void PrintIfNonzero(long? l, string s)
        {
            if(l.HasValue && l.Value > 0) {
                Console.WriteLine("  - {0}: {1}", s, l.Value);
            }
        }

        private void PrintOrderLineItem(OrderLineItem item)
        {
            Console.WriteLine("  Line item: {0}", item.Id);
            Console.WriteLine("  - Product: {0} ({1})", item.Product.Id, item.Product.Title);
            Console.WriteLine("  - Price: {0} {1}", item.Price.Value, item.Price.Currency);
            Console.WriteLine("  - Tax: {0} {1}", item.Tax.Value, item.Tax.Currency);
            if (item.ShippingDetails != null)
            {
                Console.WriteLine("  - Ship by date: {0}", item.ShippingDetails.ShipByDate);
                Console.WriteLine("  - Deliver by date: {0}", item.ShippingDetails.DeliverByDate);
                Console.WriteLine("  - Deliver via {0} {1} ({2} - {3} days)",
                    item.ShippingDetails.Method.Carrier, item.ShippingDetails.Method.MethodName,
                    item.ShippingDetails.Method.MinDaysInTransit,
                    item.ShippingDetails.Method.MaxDaysInTransit);
            }
            if (item.ReturnInfo != null && item.ReturnInfo.IsReturnable == true)
            {
                Console.WriteLine("  - Item is returnable.");
                Console.WriteLine("    - Days to return: {0}", item.ReturnInfo.DaysToReturn);
                Console.WriteLine("    - Return policy is at {0}.", item.ReturnInfo.PolicyUrl);
            }
            else
            {
                Console.WriteLine("  - Item is not returnable.");
            }
            PrintIfNonzero(item.QuantityOrdered, "Quantity Ordered");
            PrintIfNonzero(item.QuantityPending, "Quantity Pending");
            PrintIfNonzero(item.QuantityCanceled, "Quantity Canceled");
            PrintIfNonzero(item.QuantityShipped, "Quantity Shipped");
            PrintIfNonzero(item.QuantityDelivered, "Quantity Delivered");
            PrintIfNonzero(item.QuantityReturned, "Quantity Returned");
            if (item.Cancellations != null && item.Cancellations.Count > 0)
            {
                Console.WriteLine("  - {0} cancellations(s):", item.Cancellations.Count);
                foreach (var cancel in item.Cancellations)
                {
                    Console.WriteLine("    Cancellation:");
                    if (cancel.Actor != null)
                    {
                        Console.WriteLine("    - Actor: {0}", cancel.Actor);
                    }
                    Console.WriteLine("    - Creation date: {0}", cancel.CreationDate);
                    Console.WriteLine("    - Quantity: {0}", cancel.Quantity);
                    Console.WriteLine("    - Reason: {0}", cancel.Reason);
                    Console.WriteLine("    - Reason text: {0}", cancel.ReasonText);
                }
            }
            if (item.Returns != null && item.Returns.Count > 0)
            {
                Console.WriteLine("  - {0} return(s):", item.Returns.Count);
                foreach (var ret in item.Returns)
                {
                    Console.WriteLine("    Return:");
                    if (ret.Actor != null)
                    {
                        Console.WriteLine("    - Actor: {0}", ret.Actor);
                    }
                    Console.WriteLine("    - Creation date: {0}", ret.CreationDate);
                    Console.WriteLine("    - Quantity: {0}", ret.Quantity);
                    Console.WriteLine("    - Reason: {0}", ret.Reason);
                    Console.WriteLine("    - Reason text: {0}", ret.ReasonText);
                }
            }
        }
    }
}

