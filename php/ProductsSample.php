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
    $exampleProductId = 'book123';
    $exampleProduct = $this->createExampleProduct($exampleProductId);

    $this->insertProduct($exampleProduct);
    $this->getProduct($exampleProductId);
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
    $response = $this->service->products->insert($this->merchantId, $product);

    // Our example product generator does not set a product_type, so we should
    // get at least one warning.
    $warnings = $response->getWarnings();
    print ('Product created, there are ' . count($warnings) . " warnings\n");
    foreach($warnings as $warning) {
      printf(" [%s] %s\n", $warning->getReason(), $warning->getMessage());
    }
  }

  public function getProduct($offerId) {
    $productId = $this->buildProductId($offerId);
    $product = $this->service->products->get($this->merchantId, $productId);
    printf("Retrieved product %s: '%s'\n", $product->getId(),
        $product->getTitle());
  }

  public function updateProduct(
      Google_Service_ShoppingContent_Product $product) {
    // Let's fix the warning about product_type and update the product
    $product->setProductType('English/Classics');
    // Notice that we use insert. The products service does not have an update
    // method. Inserting a product with an ID that already exists means the same
    // as doing an update anyway.
    $response = $this->service->products->insert($this->merchantId, $product);

    // We should no longer get the product_type warning.
    $warnings = $response->getWarnings();
    printf("Product updated, there are now %d warnings\n", count($warnings));
    foreach($warnings as $warning) {
      printf(" [%s] %s\n", $warning->getReason(), $warning->getMessage());
    }
  }

  public function deleteProduct($offerId) {
    $productId = $this->buildProductId($offerId);
    // The response for a successful delete is empty
    $this->service->products->delete($this->merchantId, $productId);
  }

  public function insertProductBatch($products) {
    $entries = [];

    foreach ($products as $key => $product) {
      $entry =
          new Google_Service_ShoppingContent_ProductsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setProduct($product);
      $entry->setMerchantId($this->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponse = $this->service->products->custombatch($batchRequest);

    printf("Inserted %d products.\n", count($batchResponse->entries));

    foreach ($batchResponse->entries as $entry) {
      if (empty($entry->getErrors())) {
        $product = $entry->getProduct();
        printf("Inserted product %s with %d warnings\n", $product->getOfferId(),
            count($product->getWarnings()));
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
    $parameters = array('maxResults' => self::BATCH_SIZE - 1);
    $products =
        $this->service->products->listProducts($this->merchantId, $parameters);
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
      $products = $this->service->products->listProducts($this->merchantId,
          $parameters);
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
      $entry->setMerchantId($this->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponses = $this->service->products->custombatch($batchRequest);
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
    $product->setLink($this->websiteUrl . '/tale-of-two-cities.html');
    $product->setImageLink($this->websiteUrl . '/tale-of-two-cities.jpg');
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

    $product->setShipping(array($shipping));

    $shippingWeight =
        new Google_Service_ShoppingContent_ProductShippingWeight();
    $shippingWeight->setValue(200);
    $shippingWeight->setUnit('grams');

    $product->setShippingWeight($shippingWeight);

    return $product;
  }
}

$sample = new ProductsSample();
$sample->run();
