package main

import (
	"fmt"
	"math/rand"
	"net/url"
	"path"
	"strconv"

	"golang.org/x/net/context"
	"google.golang.org/api/content/v2"
)

// ordersDemo runs a demo of the Orders service by creating a test
// order and running it through its paces.  Unlike other demos, this one
// uses the v2sandbox endpoint so that the test methods are available
// and to ensure it doesn't accidentally mutate any real orders.
func ordersDemo(ctx context.Context, service *content.APIService, config *merchantInfo) {
	if config.IsMCA {
		fmt.Println("This demo cannot be run on a multi-client account.")
		return
	}
	// Local copy to avoid clobbering shared service endpoint.
	sandboxService := *service
	// If the endpoint ends in /v2/, need to change to /v2sandbox/.
	serviceURL, _ := url.Parse(sandboxService.BasePath)
	servicePath, version := path.Split(path.Clean(serviceURL.Path))
	if version == "v2" {
		serviceURL.Path = path.Join(servicePath, version+"sandbox")
		sandboxService.BasePath = serviceURL.String() + "/"
	} else {
		fmt.Println("Attempting to run Orders workflow on endpoint: " + sandboxService.BasePath)
		fmt.Println("This will fail if this endpoint does not support sandbox methods.")
	}

	orders := content.NewOrdersService(&sandboxService)
	// Following is used to create operation IDs, which ensures
	// non-duplication of operations in case multiple requests are sent.
	// Operation IDs must be unique among operations performed
	// on a given order.
	nonce := 0
	newOperationID := func() (str string) {
		str = strconv.Itoa(nonce)
		nonce++
		return
	}

	// NOTE: Normally real orders would be created by Google, but
	// in these samples, we use the sandbox's ability to create test
	// orders that we can advance the status of as needed to try things.
	fmt.Print("Creating test order... ")
	createReq := &content.OrdersCreateTestOrderRequest{TemplateName: "template1"}
	createResp, err := orders.Createtestorder(config.MerchantID, createReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Creating test order failed")
	}
	orderID := createResp.OrderId
	fmt.Println("done.")
	fmt.Printf("Order %q created.\n", orderID)
	fmt.Println()

	getCurrentOrderState := func() *content.Order {
		fmt.Printf("Retrieving order %q... ", orderID)
		order, err := orders.Get(config.MerchantID, orderID).Do()
		if err != nil {
			dumpAPIErrorAndStop(err, "Retrieving order failed")
		}
		fmt.Println("done.")
		return order
	}

	testOrder := getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// Only list unacknowledged orders.  The idea here is that as a
	// merchant, we should already have imported the information about
	// any orders we've already acknowledged, so these will be the ones
	// we've not seen yet.
	fmt.Println("Listing unacknowledged orders.")
	if err := orders.List(config.MerchantID).Acknowledged(false).Pages(ctx, printOrdersPage); err != nil {
		dumpAPIErrorAndStop(err, "Listing orders failed")
	}
	fmt.Println()

	fmt.Printf("Acknowledging order %q... ", orderID)
	ackReq := &content.OrdersAcknowledgeRequest{
		OperationId: newOperationID(),
	}
	ackResp, err := orders.Acknowledge(config.MerchantID, orderID, ackReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Acknowledging order failed")
	}
	fmt.Printf("done with status %q.\n", ackResp.ExecutionStatus)
	fmt.Println()

	merchantOrderID := strconv.Itoa(rand.Int())
	fmt.Printf("Updating merchant order ID to %q... ", merchantOrderID)
	mercOrdIDReq := &content.OrdersUpdateMerchantOrderIdRequest{
		MerchantOrderId: merchantOrderID,
		OperationId:     newOperationID(),
	}
	mercOrdIDResp, err := orders.Updatemerchantorderid(config.MerchantID, orderID, mercOrdIDReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating merchant order ID failed")
	}
	fmt.Printf("done with status %q.\n", mercOrdIDResp.ExecutionStatus)
	fmt.Println()

	// Retrieve the order using the now-set merchant order ID, instead of
	// the Google-supplied order ID.
	fmt.Printf("Retrieving merchant order %q... ", merchantOrderID)
	getMercOrdIDResp, err := orders.Getbymerchantorderid(config.MerchantID, merchantOrderID).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Retrieving by merchant order ID failed")
	}
	fmt.Println("done.")
	testOrder = getMercOrdIDResp.Order
	printOrder(testOrder)
	fmt.Println()

	// Only one left, so we cancel one of the first items from the order
	fmt.Print("Canceling one order of first item... ")
	cancelReq := &content.OrdersCancelLineItemRequest{
		LineItemId:  testOrder.LineItems[0].Id,
		OperationId: newOperationID(),
		Quantity:    1,
		Reason:      "noInventory",
		ReasonText:  "No stocked inventory of item.",
	}
	cancelResp, err := orders.Cancellineitem(config.MerchantID, orderID, cancelReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Canceling returned item failed")
	}
	fmt.Printf("done with status %q.\n", cancelResp.ExecutionStatus)

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// NOTE: This is another operation that's specifically for the
	// sandbox.  It will move an order from "inProgress" to
	// "pendingShipment" (that is, skipping the cancellable part
	// of the order process).  In the non-sandbox world, Google
	// would move an order to "pendingShipment" after giving the
	// customer time to cancel the order. (Once in this state, the
	// order can no longer be cancelled by the customer.)
	fmt.Print("Advancing test order... ")
	if _, err := orders.Advancetestorder(config.MerchantID, orderID).Do(); err != nil {
		dumpAPIErrorAndStop(err, "Advancing test order failed")
	}
	fmt.Println("done.")
	fmt.Println()

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// To simulate partial fulfillment, we'll pick the first line
	// item from our test order and ship it the pending amount.
	fmt.Print("Notifying Google about shipment of first line item... ")
	item := testOrder.LineItems[0]
	shipItem := content.OrderShipmentLineItemShipment{
		LineItemId: item.Id,
		Quantity:   item.QuantityPending,
	}
	// Going to need shipment/tracking ID later, so will create new
	// versions of this for each line item.
	shipReq1 := &content.OrdersShipLineItemsRequest{
		LineItems:   []*content.OrderShipmentLineItemShipment{&shipItem},
		OperationId: newOperationID(),
		Carrier:     item.ShippingDetails.Method.Carrier,
		ShipmentId:  "First Item",
		TrackingId:  strconv.FormatInt(rand.Int63(), 16),
	}
	shipResp, err := orders.Shiplineitems(config.MerchantID, orderID, shipReq1).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Shipping first line item failed")
	}
	fmt.Printf("done with status %q.\n", shipResp.ExecutionStatus)
	fmt.Println()

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// Let's ship the rest now.
	fmt.Print("Notifying Google about shipment of second line item... ")
	item = testOrder.LineItems[1]
	shipItem = content.OrderShipmentLineItemShipment{
		LineItemId: item.Id,
		Quantity:   item.QuantityPending,
	}
	shipReq2 := &content.OrdersShipLineItemsRequest{
		LineItems:   []*content.OrderShipmentLineItemShipment{&shipItem},
		OperationId: newOperationID(),
		Carrier:     item.ShippingDetails.Method.Carrier,
		ShipmentId:  "Second Item",
		TrackingId:  strconv.FormatInt(rand.Int63(), 16),
	}
	shipResp, err = orders.Shiplineitems(config.MerchantID, orderID, shipReq2).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Shipping second line item failed")
	}
	fmt.Printf("done with status %q.\n", shipResp.ExecutionStatus)
	fmt.Println()

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// First line item arrives.
	fmt.Print("Notifying Google about delivery of first line item... ")
	shipUpdateReq := &content.OrdersUpdateShipmentRequest{
		Status:      "delivered",
		Carrier:     shipReq1.Carrier,
		OperationId: newOperationID(),
		ShipmentId:  shipReq1.ShipmentId,
		TrackingId:  shipReq1.TrackingId,
	}
	shipUpdateResp, err := orders.Updateshipment(config.MerchantID, orderID, shipUpdateReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating first line item shipment failed")
	}
	fmt.Printf("done with status %q.\n", shipUpdateResp.ExecutionStatus)
	fmt.Println()

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// Second line item arrives.
	fmt.Print("Notifying Google about delivery of second line item... ")
	shipUpdateReq = &content.OrdersUpdateShipmentRequest{
		Status:      "delivered",
		Carrier:     shipReq2.Carrier,
		OperationId: newOperationID(),
		ShipmentId:  shipReq2.ShipmentId,
		TrackingId:  shipReq2.TrackingId,
	}
	shipUpdateResp, err = orders.Updateshipment(config.MerchantID, orderID, shipUpdateReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Updating second line item shipment failed")
	}
	fmt.Printf("done with status %q.\n", shipUpdateResp.ExecutionStatus)
	fmt.Println()

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()

	// Customer returns one of the first line item because it's malfunctioning.
	fmt.Print("Customer returns one of first line item... ")
	returnReq := &content.OrdersReturnLineItemRequest{
		LineItemId:  testOrder.LineItems[0].Id,
		OperationId: newOperationID(),
		Quantity:    1,
		Reason:      "productArrivedDamaged",
		ReasonText:  "Item malfunctioning on receipt.",
	}
	returnResp, err := orders.Returnlineitem(config.MerchantID, orderID, returnReq).Do()
	if err != nil {
		dumpAPIErrorAndStop(err, "Returning first line item failed")
	}
	fmt.Printf("done with status %q.\n", returnResp.ExecutionStatus)

	testOrder = getCurrentOrderState()
	printOrder(testOrder)
	fmt.Println()
}

func printOrdersPage(resp *content.OrdersListResponse) error {
	for _, order := range resp.Resources {
		printOrder(order)
	}
	return nil
}

func printOrder(order *content.Order) {
	fmt.Printf("Order %q:\n", order.Id)
	fmt.Printf("- Status: %s\n", order.Status)
	fmt.Printf("- Merchant: %d\n", order.MerchantId)
	fmt.Printf("- Merchant order ID: %s\n", order.MerchantOrderId)
	if customer := order.Customer; customer != nil {
		fmt.Println("- Customer information:")
		fmt.Printf("  - Full name: %s\n", customer.FullName)
		fmt.Printf("  - Email: %s\n", customer.Email)
	}
	fmt.Printf("- Placed on date: %s\n", order.PlacedDate)
	if amount := order.NetAmount; amount != nil {
		fmt.Printf("- Net amount: %s %s\n", amount.Value, amount.Currency)
	}
	fmt.Printf("- Payment status: %s\n", order.PaymentStatus)
	if method := order.PaymentMethod; method != nil {
		fmt.Println("- Payment method:")
		fmt.Printf("  - Type: %s\n", method.Type)
		fmt.Printf("  - Expiration date: %d/%d\n", method.ExpirationMonth, method.ExpirationYear)
	}
	if order.Acknowledged {
		fmt.Println("- Acknowledged: yes")
	} else {
		fmt.Println("- Acknowledged: no")
	}
	fmt.Printf("- %d line items:\n", len(order.LineItems))
	for _, item := range order.LineItems {
		printOrderLineItem(item, "  ")
	}
	fmt.Printf("- Shipping option: %s\n", order.ShippingOption)
	if cost := order.ShippingCost; cost != nil {
		fmt.Printf("- Shipping cost: %s %s\n", cost.Value, cost.Currency)
	}
	if tax := order.ShippingCostTax; tax != nil {
		fmt.Printf("- Shipping cost tax: %s %s\n", tax.Value, tax.Currency)
	}
	if len(order.Shipments) > 0 {
		fmt.Printf("- %d shipments:\n", len(order.Shipments))
		for _, shipment := range order.Shipments {
			printOrderShipment(shipment, "  ")
		}
	}
}

func printOrderLineItem(item *content.OrderLineItem, indent string) {
	fmt.Printf(indent+"Line item %q:\n", item.Id)
	fmt.Printf(indent+"- Product: %s (%s)\n", item.Product.Id, item.Product.Title)
	fmt.Printf(indent+"- Price: %s %s\n", item.Price.Value, item.Price.Currency)
	fmt.Printf(indent+"- Tax: %s %s\n", item.Tax.Value, item.Tax.Currency)

	printIfNonzero := func(count int64, text string) {
		if count > 0 {
			fmt.Printf(indent+"- %s: %d\n", text, count)
		}
	}
	printIfNonzero(item.QuantityOrdered, "Quantity ordered")
	printIfNonzero(item.QuantityPending, "Quantity pending")
	printIfNonzero(item.QuantityShipped, "Quantity shipped")
	printIfNonzero(item.QuantityDelivered, "Quantity delivered")
	printIfNonzero(item.QuantityReturned, "Quantity returned")
	printIfNonzero(item.QuantityCanceled, "Quantity canceled")
	if item.ShippingDetails != nil {
		fmt.Printf(indent+"- Ship by %s\n", item.ShippingDetails.ShipByDate)
		fmt.Printf(indent+"- Deliver by %s\n", item.ShippingDetails.DeliverByDate)
		m := item.ShippingDetails.Method
		fmt.Printf(indent+"- Delivery via %s %s (%d - %d days)\n", m.Carrier, m.MethodName, m.MinDaysInTransit, m.MaxDaysInTransit)
	}
	if item.ReturnInfo.IsReturnable {
		fmt.Println(indent + "- Item is returnable.")
		fmt.Printf(indent+"- Days to return: %d\n", item.ReturnInfo.DaysToReturn)
		fmt.Printf(indent+"- Return policy is at %s.\n", item.ReturnInfo.PolicyUrl)
	} else {
		fmt.Println(indent + "- Item is not returnable.")
	}
	if len(item.Returns) > 0 {
		fmt.Printf(indent+"- %d returns:\n", len(item.Returns))
		for n, ret := range item.Returns {
			printOrderReturn(ret, n, indent+"  ")
		}
	}
}

func printOrderReturn(ret *content.OrderReturn, index int, indent string) {
	fmt.Printf(indent+"Return #%d\n", index)
	fmt.Printf(indent+"- Actor: %s\n", ret.Actor)
	fmt.Printf(indent+"- Creation date: %s\n", ret.CreationDate)
	fmt.Printf(indent+"- Quantity: %d\n", ret.Quantity)
	fmt.Printf(indent+"- Reason: %s\n", ret.Reason)
	fmt.Printf(indent+"- Reason text: %s\n", ret.ReasonText)
}

func printOrderShipment(shipment *content.OrderShipment, indent string) {
	fmt.Printf(indent+"Shipment %q:\n", shipment.Id)
	fmt.Printf(indent+"- Creation date: %s\n", shipment.CreationDate)
	fmt.Printf(indent+"- Carrier: %s\n", shipment.Carrier)
	fmt.Printf(indent+"- Tracking ID: %s\n", shipment.TrackingId)
	fmt.Printf(indent+"- %d line items:\n", len(shipment.LineItems))
	for _, item := range shipment.LineItems {
		fmt.Printf(indent+"  %d of item %q\n", item.Quantity, item.LineItemId)
	}
	if shipment.DeliveryDate != "" {
		fmt.Printf(indent+"- Delivery date: %s\n", shipment.DeliveryDate)
	}
}
