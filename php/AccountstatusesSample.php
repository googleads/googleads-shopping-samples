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
// Accountstatuses service.
class AccountstatusesSample extends BaseSample {

  public function run() {
    printf("Retrieving merchant center information for %s\n",
        $this->merchantId);
    $this->getAccountstatus($this->merchantId);
    printf("Retrieving subaccount information for %s\n", $this->merchantId);
    $this->listAccountstatuses();
  }

  public function getAccountstatus($accountId) {
    if ($accountId != $this->merchantId) {
      $this->mustBeMCA('Non-multi-client accounts can only get their own '
          . 'information.');
      return;
    }
    $status = $this->service->accountstatuses->get(
        $this->merchantId, $accountId);
    $this->printAccountstatus($status);
  }

  public function listAccountstatuses() {
    $this->mustBeMCA('Only multi-client accounts have subaccounts.');
    $parameters = [];
    do {
      $accountStatuses = $this->service->accountstatuses->listAccountstatuses(
          $this->merchantId, $parameters);
      foreach ($accountStatuses->getResources() as $status) {
        $this->printAccountstatus($status);
      }
      if(empty($accountStatuses->getNextPageToken())) {
        break;
      }
      $parameters['pageToken'] = $accountStatuses->nextPageToken;
    } while (true);
  }

  public function printAccountstatus($status) {
    printf("Information for account %s:\n", $status->getAccountId());
    if (empty($status->getDataQualityIssues())) {
      print("- No data quality issues.\n");
    } else {
      printf("- There are %d data quality issues:\n",
          count($status->getDataQualityIssues()));
      foreach ($status->getDataQualityIssues() as $issue) {
        printf("  - (%s) %s\n", $issue->getSeverity(), $issue->getId());
        printf("    Affects %d products, %d examples follow:\n",
            $issue->getNumItems(), count($issue->getExampleItems()));
        foreach ($issue->getExampleItems() as $example) {
          printf("    - Item %s: %s\n", $example->getItemId(),
              $example->getTitle());
        }
      }
    }
  }
}

$sample = new AccountstatusesSample();
$sample->run();
