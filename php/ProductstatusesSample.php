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
    if (!$this->session->mcaStatus) {
      $this->listProductstatuses();
    } else {
      print "This demo contains no operations for a multi-client account.\n";
    }
  }

  public function getProductstatus($itemId) {
    $this->session->mustNotBeMCA(
        'Multi-client accounts cannot contain products.');
    $status = $this->session->service->productstatuses->get(
        $this->session->merchantId, $itemId);
    $this->printProductstatus($status);
  }

  public function listProductstatuses() {
    $this->session->mustNotBeMCA(
        'Multi-client accounts cannot contain products.');
    $parameters = [];
    do {
      $statuses = $this->session->service->productstatuses->listProductstatuses(
          $this->session->merchantId, $parameters);
      foreach ($statuses->getResources() as $status) {
        $this->printProductstatus($status);
      }
      $parameters['pageToken'] = $statuses->nextPageToken;
    } while (!empty($parameters['pageToken']));
  }

  public function printProductstatus($status) {
    printf("Information for product %s:\n", $status->getProductId());
    printf("- Title: %s\n", $status->getTitle());
    printf("- Destination statuses:\n");
    foreach ($status->getDestinationStatuses() as $dest) {
      printf("  - Destination %s: %s\n",
          $dest->getDestination(), $dest->getStatus());
    }
    if (empty($status->getItemLevelIssues())) {
      print "\n- No issues.\n";
    } else {
      printf("\n- There are %d issues:\n",
          count($status->getItemLevelIssues()));
      foreach ($status->getItemLevelIssues() as $issue) {
        printf("  - Code: %s\n", $issue->getCode());
        printf("    Description: %s\n", $issue->getDescription());
        printf("    Detailed description: %s\n", $issue->getDetail());
        printf("    Resolution: %s\n", $issue->getResolution());
        printf("    Servability: %s\n", $issue->getServability());
      }
    }
  }
}
