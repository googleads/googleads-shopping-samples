<?php

require_once 'BaseSample.php';

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
    $example_product_id = 'book123';
    $example_product = $this->createExampleProduct($example_product_id);

    $this->insertProduct($example_product);
    $this->getProduct($example_product_id);
    $this->updateProduct($example_product);
    $this->deleteProduct($example_product_id);

    $example_product_batch_ids = array();

    for ($i = 0; $i < self::BATCH_SIZE; $i++) {
      $example_product_batch_ids[] = 'book' . $i;
    }

    $example_product_batch =
        $this->createExampleProducts($example_product_batch_ids);

    $this->insertProductBatch($example_product_batch);
    $this->listProducts();
    $this->deleteProductBatch($example_product_batch_ids);
  }

  public function insertProduct(
      Google_Service_ShoppingContent_Product $product) {
    $response = $this->service->products->insert($this->merchant_id, $product);

    // Our example product generator does not set a product_type, so we should
    // get at least one warning.
    $warnings = $response->getWarnings();
    print ('Product created, there are ' . count($warnings) . " warnings\n");
    foreach($warnings as $warning) {
      printf(" [%s] %s\n", $warning->getReason(), $warning->getMessage());
    }
  }

  public function getProduct($offer_id) {
    $product_id = $this->buildProductId($offer_id);
    $product = $this->service->products->get($this->merchant_id, $product_id);
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
    $response = $this->service->products->insert($this->merchant_id, $product);

    // We should get one fewer warning now
    $warnings = $response->getWarnings();
    printf("Product updated, there are now %d warnings\n", count($warnings));
    foreach($warnings as $warning) {
      printf(" [%s] %s\n", $warning->getReason(), $warning->getMessage());
    }
  }

  public function deleteProduct($offer_id) {
    $product_id = $this->buildProductId($offer_id);
    // The response for a successful delete is empty
    $this->service->products->delete($this->merchant_id, $product_id);
  }

  public function insertProductBatch($products) {
    $entries = array();

    foreach ($products as $key => $product) {
      $entry =
          new Google_Service_ShoppingContent_ProductsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setProduct($product);
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_response = $this->service->products->custombatch($batch_request);

    printf("Inserted %d products.\n", count($batch_response->entries));

    foreach ($batch_response->entries as $entry) {
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
        $this->service->products->listProducts($this->merchant_id, $parameters);
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
      $products = $this->service->products->listProducts($this->merchant_id,
          $parameters);
    }
  }

  public function deleteProductBatch($offer_ids) {
    $entries = array();

    foreach ($offer_ids as $key => $offer_id) {
      $entry =
          new Google_Service_ShoppingContent_ProductsCustomBatchRequestEntry();
      $entry->setMethod('delete');
      $entry->setBatchId($key);
      $entry->setProductId($this->buildProductId($offer_id));
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_ProductsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_responses = $this->service->products->custombatch($batch_request);
    $errors = 0;
    foreach ($batch_responses->entries as $entry) {
      if (!empty($entry->getErrors())) {
        $errors++;
      }
    }
    print "Requested delete of batch inserted test products\n";
    printf("There were %d errors\n", $errors);
  }

  private function buildProductId($offer_id) {
    return sprintf('%s:%s:%s:%s', self::CHANNEL, self::CONTENT_LANGUAGE,
      self::TARGET_COUNTRY, $offer_id);
  }

  private function createExampleProducts($offer_ids) {
    $products = array();

    foreach ($offer_ids as $offer_id) {
      $products[] = $this->createExampleProduct($offer_id);
    }

    return $products;
  }

  private function createExampleProduct($offer_id) {
    $product = new Google_Service_ShoppingContent_Product();

    $product->setOfferId($offer_id);
    $product->setTitle('A Tale of Two Cities');
    $product->setDescription('A classic novel about the French Revolution');
    $product->setLink('http://my-book-shop.com/tale-of-two-cities.html');
    $product->setImageLink('http://my-book-shop.com/tale-of-two-cities.jpg');
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

$sample = new ProductsSample();
$sample->run();
