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
// Products service.
class ProductsSample extends BaseSample {
  // These constants define the identifiers for all of our example products
  // The products will be sold online
  const CHANNEL = 'online';
  // The product details are provided in English
  const CONTENT_LANGUAGE = 'en';
  // The products are sold in the United States
  const TARGET_COUNTRY = 'US';

  // This constant defines how many example products to create in a batch
  const BATCH_SIZE = 10;

  public function run() {
    if (is_null($this->session->websiteUrl)) {
      throw InvalidArgumentException(
          'Cannot run Products workflow on a Merchant Center account without '
          . 'a configured website URL.');
    }
    $exampleProductId = 'book123';
    $exampleProduct = $this->createExampleProduct($exampleProductId);

    $this->insertProduct($exampleProduct);

    // There is a short period after creating a product during which it may not
    // be retrieved (get). In general use it would be unusual to do so anyway,
    // but for the purpose of this example we retry with back off.
    $this->session->retry($this, 'getProduct', $exampleProductId, 10);
    $this->updateProduct($exampleProduct);
    $this->deleteProduct($exampleProductId);

    $exampleProductBatchIDs = [];

    for ($i = 0; $i < self::BATCH_SIZE; $i++) {
      $exampleProductBatchIDs[] = 'book' . $i;
    }

    $exampleProductBatch =
      $this->createExampleProducts($exampleProductBatchIDs);
    $this->insertProductBatch($exampleProductBatch);
    $this->listProducts();
    $this->deleteProductBatch($exampleProductBatchIDs);
  }

  public function insertProduct(
      Google_Service_ShoppingContent_Product $product) {
    $response = $this->session->service->products->insert(
        $this->session->merchantId, $product);
    printf ("Product created: %s \n", $response->getId());

    // Our example product generator does not set a product_type
    // We will do it within an update call.
  }

  public function getProduct($offerId) {
    $productId = $this->buildProductId($offerId);
    $product = $this->session->service->products->get(
        $this->session->merchantId, $productId);
    printf("Retrieved product %s: %s \n", $product->getId(),
        $product->getTitle());
  }

  public function updateProduct(
      Google_Service_ShoppingContent_Product $product) {
    // Let's add a product_type and update the product
    $product->setProductTypes(['English/Classics']);
    // Notice that we use insert. The products service does not have an update
    // method. Inserting a product with an ID that already exists means the same
    // as doing an update anyway.
    $response = $this->session->service->products->insert(
        $this->session->merchantId, $product);
    printf ("Product updated: %s \n", $response->getId());

  }

  public function deleteProduct($offerId) {
    $productId = $this->buildProductId($offerId);
    // The response for a successful delete is empty
    $this->session->service->products->delete(
        $this->session->merchantId, $productId);
    printf ("Product deleted: %s \n", $offerId);
  }

  public function insertProductBatch($products) {
    $entries = [];

    foreach ($products as $key => $product) {
      $entry =
          new Google_Service_ShoppingContent_ProductsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setProduct($product);
      $entry->setMerchantId($this->session->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponse =
        $this->session->service->products->custombatch($batchRequest);

    printf("Inserted %d products.\n", count($batchResponse->entries));

    foreach ($batchResponse->entries as $entry) {
      if (empty($entry->getErrors())) {
        $product = $entry->getProduct();
      } else {
        print ("There were errors inserting a product:\n");
        foreach ($entry->getErrors()->getErrors() as $error) {
          printf("\t%s\n", $error->getMessage());
        }
      }
    }
  }

  public function listProducts() {
    // We set the maximum number of results to be lower than the number of
    // products that we inserted, to demonstrate paging.
    $parameters = ['maxResults' => self::BATCH_SIZE - 1];
    $products = $this->session->service->products->listProducts(
        $this->session->merchantId, $parameters);
    $count = 0;
    // You can fetch all items in a loop. We limit to looping just 3
    // times for this example as it may take a long time to finish if you
    // already have many products.
    while (!empty($products->getResources()) && $count++ < 3) {
      foreach ($products->getResources() as $product) {
        printf("%s %s\n", $product->getId(), $product->getTitle());
      }
      // If the result has a nextPageToken property then there are more pages
      // available to fetch
      if (empty($products->getNextPageToken())) {
        break;
      }
      // You can fetch the next page of results by setting the pageToken
      // parameter with the value of nextPageToken from the previous result.
      $parameters['pageToken'] = $products->nextPageToken;
      $products = $this->session->service->products->listProducts(
          $this->session->merchantId, $parameters);
    }
  }

  public function deleteProductBatch($offerIds) {
    $entries = [];

    foreach ($offerIds as $key => $offerId) {
      $entry =
          new Google_Service_ShoppingContent_ProductsCustomBatchRequestEntry();
      $entry->setMethod('delete');
      $entry->setBatchId($key);
      $entry->setProductId($this->buildProductId($offerId));
      $entry->setMerchantId($this->session->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponses =
        $this->session->service->products->custombatch($batchRequest);
    $errors = 0;
    foreach ($batchResponses->entries as $entry) {
      if (!empty($entry->getErrors())) {
        $errors++;
      }
    }
    print "Requested delete of batch inserted test products\n";
    printf("There were %d errors\n", $errors);
  }

  private function buildProductId($offerId) {
    return sprintf('%s:%s:%s:%s', self::CHANNEL, self::CONTENT_LANGUAGE,
      self::TARGET_COUNTRY, $offerId);
  }

  private function createExampleProducts($offerIds) {
    $products = [];

    foreach ($offerIds as $offerId) {
      $products[] = $this->createExampleProduct($offerId);
    }

    return $products;
  }

  private function createExampleProduct($offerId) {
    $product = new Google_Service_ShoppingContent_Product();

    $product->setOfferId($offerId);
    $product->setTitle('A Tale of Two Cities');
    $product->setDescription('A classic novel about the French Revolution');
    $product->setLink($this->session->websiteUrl . '/tale-of-two-cities.html');
    $product->setImageLink(
        $this->session->websiteUrl . '/tale-of-two-cities.jpg');
    $product->setContentLanguage(self::CONTENT_LANGUAGE);
    $product->setTargetCountry(self::TARGET_COUNTRY);
    $product->setChannel(self::CHANNEL);
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

    $product->setShipping([$shipping]);

    $shippingWeight =
        new Google_Service_ShoppingContent_ProductShippingWeight();
    $shippingWeight->setValue(200);
    $shippingWeight->setUnit('grams');

    $product->setShippingWeight($shippingWeight);

    return $product;
  }
}
