<?php

require_once 'BaseSample.php';

class InventorySample extends BaseSample {
  public function run() {
    // First we need to create some example products to work with. This example
    // glosses over the functionality of the products service, please see the
    // ProductsSample for more detail.
    $product1 = $this->createExampleProduct('book1');
    $product2 = $this->createExampleProduct('book2');

    $this->service->products->insert($this->merchant_id, $product1);
    $this->service->products->insert($this->merchant_id, $product2);

    // Now we create an inventory update. We will change the availability and
    // increase the price.
    $inventory = new Google_Service_ShoppingContent_InventorySetRequest();
    $inventory->setAvailability('out of stock');

    $price = new Google_Service_ShoppingContent_Price();
    $price->setValue('3.00');
    $price->setCurrency('USD');

    $inventory->setPrice($price);

    // Make the request
    $response = $this->service->inventory->set($this->merchant_id, 'online',
        'online:en:US:book1', $inventory);

    // Retrieve the product so that we can see that the values really changed.
    $updated =
        $this->service->products->get($this->merchant_id, 'online:en:US:book1');

    print ("Updated product:\n");
    printf("* Price: %s %s => %s %s\n", $product1->getPrice()->getValue(),
        $product1->getPrice()->getCurrency(), $updated->getPrice()->getValue(),
        $updated->getPrice()->getCurrency());
    printf("* Availability: %s => %s\n", $product1->getAvailability(),
        $updated->getAvailability());

    // In this example we are going to update both of our products in a single
    // batch request. We will set a special sale price of 1 USD for the whole of
    // December 2014.
    $sale_price = new Google_Service_ShoppingContent_Price();
    $sale_price->setValue('1.00');
    $sale_price->setCurrency('USD');

    // A salePriceEffectiveDate is a string containing two dates, a start and an
    // end. Both dates are ISO8601 format (YYYY-MM-DD) and are separated by a
    // space, comma or slash. Note that the separator must be a single
    // character, so you cannot use a comma followed by a space, for example. If
    // you wish to omit either date, used the literal string 'null' instead of
    // the date.
    $sale_date = '2014-12-01 2014-12-31';

    $inventory = new Google_Service_ShoppingContent_Inventory();
    $inventory->setSalePrice($sale_price);
    $inventory->setSalePriceEffectiveDate($sale_date);

    $batch_entry_1 =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequestEntry();
    $batch_entry_1->setBatchId(1);
    $batch_entry_1->setStoreCode('online');
    $batch_entry_1->setProductId('online:en:US:book1');
    $batch_entry_1->setInventory($inventory);
    $batch_entry_1->setMerchantId($this->merchant_id);

    $batch_entry_2 =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequestEntry();
    $batch_entry_2->setBatchId(2);
    $batch_entry_2->setStoreCode('online');
    $batch_entry_2->setProductId('online:en:US:book2');
    $batch_entry_2->setInventory($inventory);
    $batch_entry_2->setMerchantId($this->merchant_id);

    $batch_request =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequest();
    $batch_request->setEntries(array($batch_entry_1, $batch_entry_2));

    $batch_response = $this->service->inventory->custombatch($batch_request);

    // Tidy up after ourselves
    $this->service->products->delete($this->merchant_id, 'online:en:US:book1');
    $this->service->products->delete($this->merchant_id, 'online:en:US:book2');
  }

  private function createExampleProduct($offer_id) {
    $product = new Google_Service_ShoppingContent_Product();

    $product->setOfferId($offer_id);
    $product->setTitle('A Tale of Two Cities');
    $product->setDescription('A classic novel about the French Revolution');
    $product->setLink('http://my-book-shop.com/tale-of-two-cities.html');
    $product->setImageLink('http://my-book-shop.com/tale-of-two-cities.jpg');
    $product->setContentLanguage('en');
    $product->setTargetCountry('US');
    $product->setChannel('online');
    $product->setAvailability('in stock');
    $product->setCondition('new');
    $product->setGoogleProductCategory('Media > Books');
    $product->setGtin('9780007350896');

    $price = new Google_Service_ShoppingContent_Price();
    $price->setValue('2.50');
    $price->setCurrency('USD');

    $product->setPrice($price);

    $shipping_price = new Google_Service_ShoppingContent_Price();
    $shipping_price->setValue('0.99');
    $shipping_price->setCurrency('USD');

    $shipping = new Google_Service_ShoppingContent_ProductShipping();
    $shipping->setPrice($shipping_price);
    $shipping->setCountry('US');
    $shipping->setService('Standard shipping');

    $product->setShipping(array($shipping));

    $shipping_weight =
        new Google_Service_ShoppingContent_ProductShippingWeight();
    $shipping_weight->setValue(200);
    $shipping_weight->setUnit('grams');

    $product->setShippingWeight($shipping_weight);

    return $product;
  }
}

$sample = new InventorySample();
$sample->run();
