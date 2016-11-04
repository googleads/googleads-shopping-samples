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
// Productstatuses service.
class ProductstatusesSample extends BaseSample {

  public function run() {
    $this->mustNotBeMCA('This demo contains no operations for a multi-client '
        . 'account.');
    $this->listProductstatuses();
  }

  public function getProductstatus($itemId) {
    $this->mustNotBeMCA('Multi-client accounts cannot contain products.');
    $status = $this->service->productstatuses->get(
        $this->merchantId, $itemId);
    $this->printProductstatus($status);
  }

  public function listProductstatuses() {
    $this->mustNotBeMCA('Multi-client accounts cannot contain products.');
    $parameters = [];
    do {
      $productStatuses = $this->service->productstatuses->listProductstatuses(
          $this->merchantId, $parameters);
      foreach ($productStatuses->getResources() as $status) {
        $this->printProductstatus($status);
      }
      if(empty($productStatuses->getNextPageToken())) {
        break;
      }
      $parameters['pageToken'] = $productStatuses->nextPageToken;
    } while (true);
  }

  public function printProductstatus($status) {
    printf("Information for product %s:\n", $status->getProductId());
    printf("- Title: %s\n", $status->getTitle());
    if (empty($status->getDataQualityIssues())) {
      print "- No data quality issues.\n";
    } else {
      printf("- There are %d data quality issues:\n",
          count($status->getDataQualityIssues()));
      foreach ($status->getDataQualityIssues() as $issue) {
        if(empty($issue->getDetail())) {
          printf("  - (%s) %s\n", $issue->getSeverity(), $issue->getId());
        } else {
          printf("  - (%s) %s: %s\n", $issue->getSeverity(), $issue->getId(),
              $issue->getDetail());
        }
      }
    }
  }
}

$sample = new ProductstatusesSample();
$sample->run();
