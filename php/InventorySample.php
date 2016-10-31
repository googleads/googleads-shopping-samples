<?php
/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

require_once 'BaseSample.php';

// Class for running through some example interactions with the
// Inventory service.
class InventorySample extends BaseSample {
  public function run() {
    // First we need to create some example products to work with. This example
    // glosses over the functionality of the products service, please see the
    // ProductsSample for more detail.
    $product1 = $this->createExampleProduct('book1');
    $product2 = $this->createExampleProduct('book2');

    $this->service->products->insert($this->merchantId, $product1);
    $this->service->products->insert($this->merchantId, $product2);

    // Now we create an inventory update. We will change the availability and
    // increase the price.
    $inventory = new Google_Service_ShoppingContent_InventorySetRequest();
    $inventory->setAvailability('out of stock');

    $price = new Google_Service_ShoppingContent_Price();
    $price->setValue('3.00');
    $price->setCurrency('USD');

    $inventory->setPrice($price);

    // Make the request
    $response = $this->service->inventory->set($this->merchantId, 'online',
        'online:en:US:book1', $inventory);

    // Retrieve the product so that we can see that the values really changed.
    $updated =
        $this->service->products->get($this->merchantId, 'online:en:US:book1');

    print ("Updated product:\n");
    printf("* Price: %s %s => %s %s\n", $product1->getPrice()->getValue(),
        $product1->getPrice()->getCurrency(), $updated->getPrice()->getValue(),
        $updated->getPrice()->getCurrency());
    printf("* Availability: %s => %s\n", $product1->getAvailability(),
        $updated->getAvailability());

    // In this example we are going to update both of our products in a single
    // batch request. We will set a special sale price of 1 USD for the whole of
    // December 2014.
    $salePrice = new Google_Service_ShoppingContent_Price();
    $salePrice->setValue('1.00');
    $salePrice->setCurrency('USD');

    // A salePriceEffectiveDate is a string containing two dates, a start and an
    // end. Both dates are ISO8601 format (YYYY-MM-DD) and are separated by a
    // space, comma or slash. Note that the separator must be a single
    // character, so you cannot use a comma followed by a space, for example. If
    // you wish to omit either date, used the literal string 'null' instead of
    // the date.
    $saleDate = '2014-12-01 2014-12-31';

    $inventory = new Google_Service_ShoppingContent_Inventory();
    $inventory->setSalePrice($salePrice);
    $inventory->setSalePriceEffectiveDate($saleDate);

    $batch_entry_1 =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequestEntry();
    $batch_entry_1->setBatchId(1);
    $batch_entry_1->setStoreCode('online');
    $batch_entry_1->setProductId('online:en:US:book1');
    $batch_entry_1->setInventory($inventory);
    $batch_entry_1->setMerchantId($this->merchantId);

    $batch_entry_2 =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequestEntry();
    $batch_entry_2->setBatchId(2);
    $batch_entry_2->setStoreCode('online');
    $batch_entry_2->setProductId('online:en:US:book2');
    $batch_entry_2->setInventory($inventory);
    $batch_entry_2->setMerchantId($this->merchantId);

    $batchRequest =
        new Google_Service_ShoppingContent_InventoryCustomBatchRequest();
    $batchRequest->setEntries(array($batch_entry_1, $batch_entry_2));

    $batchResponse = $this->service->inventory->custombatch($batchRequest);

    // Tidy up after ourselves
    $this->service->products->delete($this->merchantId, 'online:en:US:book1');
    $this->service->products->delete($this->merchantId, 'online:en:US:book2');
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

    $shippingPrice = new Google_Service_ShoppingContent_Price();
    $shippingPrice->setValue('0.99');
    $shippingPrice->setCurrency('USD');

    $shipping = new Google_Service_ShoppingContent_ProductShipping();
    $shipping->setPrice($shippingPrice);
    $shipping->setCountry('US');
    $shipping->setService('Standard shipping');

    $product->setShipping(array($shipping));

    $shippingWeight =
        new Google_Service_ShoppingContent_ProductShippingWeight();
    $shippingWeight->setValue(200);
    $shippingWeight->setUnit('grams');

    $product->setShippingWeight($shippingWeight);

    return $product;
  }
}

$sample = new InventorySample();
$sample->run();
