package shopping.content.v2_1.samples.orders;

import com.google.api.services.content.model.Order;
import com.google.api.services.content.model.OrderCustomer;
import com.google.api.services.content.model.OrderLineItem;
import com.google.api.services.content.model.OrderLineItemReturnInfo;
import com.google.api.services.content.model.OrderLineItemShippingDetails;
import com.google.api.services.content.model.OrderLineItemShippingDetailsMethod;
import com.google.api.services.content.model.OrderReturn;
import com.google.api.services.content.model.OrderShipment;
import com.google.api.services.content.model.OrderShipmentLineItemShipment;
import com.google.api.services.content.model.Price;

/** Utility class for working with Order resources. */
public class OrdersUtils {
  public static void printOrder(Order order) {
    System.out.printf("Order \"%s\":%n", order.getId());
    System.out.printf("- Status: %s%n", order.getStatus());
    System.out.printf("- Merchant: %d%n", order.getMerchantId());
    System.out.printf("- Merchant order ID: %s%n", order.getMerchantOrderId());
    if (order.getCustomer() != null) {
      OrderCustomer customer = order.getCustomer();
      System.out.println("- Customer information:");
      System.out.printf("  - Full name: %s%n", customer.getFullName());
      System.out.printf("  - Email: %s%n", customer.getMarketingRightsInfo()
          .getMarketingEmailAddress());
    }
    System.out.printf("- Placed on date: %s%n", order.getPlacedDate());
    if (order.getNetPriceAmount() != null) {
      Price amount = order.getNetPriceAmount();
      System.out.printf("- Net amount: %s %s%n", amount.getValue(), amount.getCurrency());
    }
    System.out.printf("- Payment status: %s%n", order.getPaymentStatus());

    System.out.println(
        "- Acknowledged: " + (order.getAcknowledged() == Boolean.TRUE ? "yes" : "no"));
    if (order.getLineItems() != null && order.getLineItems().size() > 0) {
      System.out.printf("- %d line items:%n", order.getLineItems().size());
      for (OrderLineItem item : order.getLineItems()) {
        printOrderLineItem(item, "  ");
      }
    }
    System.out.printf("- Shipping option: %s%n", order.getShipments());
    if (order.getShippingCost() != null) {
      Price cost = order.getShippingCost();
      System.out.printf("- Shipping cost: %s %s%n", cost.getValue(), cost.getCurrency());
    }
    if (order.getShippingCostTax() != null) {
      Price tax = order.getShippingCostTax();
      System.out.printf("- Shipping cost tax: %s %s%n", tax.getValue(), tax.getCurrency());
    }
    if (order.getShipments() != null && order.getShipments().size() > 0) {
      System.out.printf("- %d shipments:%n", order.getShipments().size());
      for (OrderShipment shipment : order.getShipments()) {
        printOrderShipment(shipment, "  ");
      }
    }
  }

  private static void printOrderLineItem(OrderLineItem item, String indent) {
    System.out.printf(indent + "Line item \"%s\":%n", item.getId());
    System.out.printf(
        indent + "- Product: %s (%s)%n", item.getProduct().getId(), item.getProduct().getTitle());
    System.out.printf(
        indent + "- Price: %s %s%n", item.getPrice().getValue(), item.getPrice().getCurrency());
    System.out.printf(
        indent + "- Tax: %s %s%n", item.getTax().getValue(), item.getTax().getCurrency());
    printIfNonzero(item.getQuantityOrdered(), "Quantity ordered", indent);
    printIfNonzero(item.getQuantityPending(), "Quantity pending", indent);
    printIfNonzero(item.getQuantityShipped(), "Quantity shipped", indent);
    printIfNonzero(item.getQuantityDelivered(), "Quantity delivered", indent);
    printIfNonzero(item.getQuantityReturned(), "Quantity returned", indent);
    printIfNonzero(item.getQuantityCanceled(), "Quantity canceled", indent);
    if (item.getShippingDetails() != null) {
      OrderLineItemShippingDetails details = item.getShippingDetails();
      System.out.printf(indent + "- Ship by date: %s%n", details.getShipByDate());
      System.out.printf(indent + "- Deliver by date: %s%n", details.getDeliverByDate());
      OrderLineItemShippingDetailsMethod method = details.getMethod();
      System.out.printf(
          indent + "- Deliver via: %s %s (%s - %s days)%n",
          method.getCarrier(),
          method.getMethodName(),
          method.getMinDaysInTransit(),
          method.getMaxDaysInTransit());
    }
    if (item.getReturnInfo() != null && item.getReturnInfo().getIsReturnable() == Boolean.TRUE) {
      OrderLineItemReturnInfo info = item.getReturnInfo();
      System.out.println(indent + "- Item is returnable.");
      System.out.printf(indent + "  - Days to return: %d%n", info.getDaysToReturn());
      System.out.printf(indent + "  - Return policy is at %s.%n", info.getPolicyUrl());
    } else {
      System.out.println(indent + "- Item is not returnable.");
    }
    if (item.getReturns() != null && item.getReturns().size() > 0) {
      for (OrderReturn ret : item.getReturns()) {
        printOrderReturn(ret, indent + "  ");
      }
    }
  }

  private static void printOrderReturn(OrderReturn ret, String indent) {
    System.out.println(indent + "Return:");
    if (ret.getActor() != null) {
      System.out.printf(indent + "- Actor: %s%n", ret.getActor());
    }
    System.out.printf(indent + "- Creation date: %s%n", ret.getCreationDate());
    System.out.printf(indent + "- Quantity: %d%n", ret.getQuantity());
    System.out.printf(indent + "- Reason: %s%n", ret.getReason());
    System.out.printf(indent + "- Reason text: %s%n", ret.getReasonText());
  }

  private static void printIfNonzero(long count, String text, String indent) {
    if (count > 0) {
      System.out.printf(indent + "- %s: %d%n", text, count);
    }
  }

  private static void printOrderShipment(OrderShipment shipment, String indent) {
    System.out.printf(indent + "Shipment \"%s\":%n", shipment.getId());
    System.out.printf(indent + "- Creation date: %s%n", shipment.getCreationDate());
    System.out.printf(indent + "- Carrier: %s%n", shipment.getCarrier());
    System.out.printf(indent + "- Tracking ID: %s%n", shipment.getTrackingId());
    if (shipment.getLineItems() != null && shipment.getLineItems().size() > 0) {
      System.out.printf(indent + "- %d line items:%n", shipment.getLineItems().size());
      for (OrderShipmentLineItemShipment item : shipment.getLineItems()) {
        System.out.printf(
            indent + "  %d of item \"%s\"%n", item.getQuantity(), item.getLineItemId());
      }
    }
    if (shipment.getDeliveryDate() != null && !shipment.getDeliveryDate().equals("")) {
      System.out.printf(indent + "- Delivery date: %s%n", shipment.getDeliveryDate());
    }
  }
}
