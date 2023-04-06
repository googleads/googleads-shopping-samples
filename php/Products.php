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
class Products extends BaseSample {

  const CHANNEL = 'online';
  const BATCH_SIZE = 200;
  const NUMBER_LOOPS = 300;
  const PRICE_CSV_URL = "https://feeds.pricio.de/subfeeds/preisupdate_f24_google.csv";
  const AVAILABILITY_CSV_URL = "https://feeds.pricio.de/subfeeds/preisupdate_f24_google.csv";
  public $csvPrices = [];
  public $csvAvailabilities = [];

  function __construct() {
      parent::__construct();
      $dataPrice = file_get_contents(self::PRICE_CSV_URL);
      $rowsPrice = explode("\n",$dataPrice);
      $s = array();
      foreach ($rowsPrice as $rowPrice) {
          $valPrice = str_getcsv($rowPrice)[0];
          $val_arrPrice = explode (";", $valPrice);
          if(isset($val_arrPrice[0]) && isset($val_arrPrice[1])){
            $s[$val_arrPrice[0]] = $val_arrPrice[1];
          }
      }
      $this->csvPrices = $s;


      $dataAvailability = file_get_contents(self::AVAILABILITY_CSV_URL);
      $rowsAvailability = explode("\n",$dataAvailability);
      $s = array();
      foreach ($rowsAvailability as $rowAvailability) {
          $valAvailability = str_getcsv($rowAvailability)[0];
          $val_arrAvailability = explode (";", $valAvailability);
          if(isset($val_arrAvailability[0]) && isset($val_arrAvailability[1])){
            $s[$val_arrAvailability[0]] = $val_arrAvailability[1];
          }
      }
      $this->csvAvailabilities = $s;



  }

  public function run(){
    return 0;
  }
  public function getProduct($offerId, $contentLanguage, $targetCountry) {
    $productId = $this->buildProductId($offerId, $contentLanguage, $targetCountry);
    $product = $this->session->service->products->get($this->session->merchantId, $productId);
    return $product;
  }

  public function buildProductId($offerId, $contentLanguage, $targetCountry) {
    return sprintf('%s:%s:%s:%s', self::CHANNEL, $contentLanguage, $targetCountry, $offerId);
  }

  public function updateProduct(Google_Service_ShoppingContent_Product $product) {
    $response = $this->session->service->products->insert($this->session->merchantId, $product);
    return $response;
  }

  public function getAllProducts(){
    $allProducts = [];
    $parameters = ['maxResults' => self::BATCH_SIZE - 1];
    $products = $this->session->service->products->listProducts(
        $this->session->merchantId, $parameters);
    $count = 0;
    while (!empty($products->getResources()) && $count++ < self::NUMBER_LOOPS) {
      foreach ($products->getResources() as $product) {
        $allProducts[] = $product;
      }
      if (empty($products->getNextPageToken())) {
        break;
      }
      $parameters['pageToken'] = $products->nextPageToken;
      $products = $this->session->service->products->listProducts(
          $this->session->merchantId, $parameters);
    }
    return $allProducts;
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


}
