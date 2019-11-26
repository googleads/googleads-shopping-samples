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
        $this->session->merchantId);
    $this->getAccountstatus($this->session->merchantId);
    if ($this->session->mcaStatus) {
      printf("Retrieving subaccount information for %s\n",
          $this->session->merchantId);
      $this->listAccountstatuses();
    }
  }

  public function getAccountstatus($accountId) {
    if ($accountId != $this->session->merchantId) {
      $this->session->mustBeMCA(
          'Non-multi-client accounts can only get their own information.');
      return;
    }
    $status = $this->session->service->accountstatuses->get(
        $this->session->merchantId, $accountId);
    $this->printAccountstatus($status);
  }

  public function listAccountstatuses() {
    $this->session->mustBeMCA('Only multi-client accounts have subaccounts.');
    $parameters = [];
    do {
      $statuses = $this->session->service->accountstatuses->listAccountstatuses(
          $this->session->merchantId, $parameters);
      foreach ($statuses->getResources() as $status) {
        $this->printAccountstatus($status);
      }
      $parameters['pageToken'] = $statuses->nextPageToken;
    } while (!empty($parameters['pageToken']));
  }

  public function printAccountstatus($status) {
    printf("Information for account %s:\n", $status->getAccountId());
    if (empty($status->getAccountLevelIssues())) {
      print "- No account quality issues.\n";
    } else {
      printf("- There are %d account quality issues:\n",
          count($status->getAccountLevelIssues()));
      foreach ($status->getAccountLevelIssues() as $issue) {
        printf('  - [%s] %s ', $issue->getSeverity(), $issue->getId());
        if (!empty($issue->getCountry())) {
          printf('  - Country: %s ', $issue->getCountry());
        }
        if (!empty($issue->getDestination())) {
          printf('  - Destination: %s ', $issue->getDestination());
        }
        if (!empty($issue->getTitle())) {
          printf("\n    - %s ", $issue->getTitle());
        }
        if (!empty($issue->getDetail())) {
          printf("\n    - %s ", $issue->getDetail());
        }
        if (!empty($issue->getDocumentation())) {
          printf("\n    - %s\n\n", $issue->getDocumentation());
        }
      }
    }
  }
}
