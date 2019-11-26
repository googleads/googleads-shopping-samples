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
// Accounts service, specifically those interactions that require
// a Multi-Client Account.
class MultiClientAccountsSample extends BaseSample {
  // This constant defines how many example accounts to create in a batch
  const BATCH_SIZE = 10;

  public function run() {
    $this->session->mustBeMCA();

    $exampleAccountName = 'account123';
    $exampleAccountBatchNames = [];

    for ($i = 0; $i < self::BATCH_SIZE; $i++) {
      $exampleAccountBatchNames[] = 'account' . $i;
    }

    $exampleAccount = $this->createExampleAccount($exampleAccountName);
    $exampleAccount = $this->insertAccount($exampleAccount);
    $exampleAccountId = $exampleAccount->getId();

    $exampleAccountBatch =
        $this->createExampleAccounts($exampleAccountBatchNames);
    $exampleAccountBatchIds =
        $this->insertAccountBatch($exampleAccountBatch);

    $this->listAccounts();

    // There is a short period after creating an account during which the
    // account can be listed with the listAccounts method but is not yet able to
    // be retrieved or modified directly. For the purposes of this example we
    // wish to create an account and then retrieve, update and delete it
    // immediately, something you would not normally do.
    //
    // To achieve this we retry each method up to 5 times with an exponential
    // back-off.
    $this->session->retry($this, 'getAccount', $exampleAccountId);
    $this->session->retry($this, 'updateAccount', $exampleAccount);
    $this->session->retry($this, 'deleteAccount', $exampleAccountId);

    $this->deleteAccountBatch($exampleAccountBatchIds);
  }

  public function insertAccount(
      Google_Service_ShoppingContent_Account $account) {
    $response = $this->session->service->accounts->insert(
        $this->session->merchantId, $account);

    printf("Created a new account, '%s', with ID %d\n", $response->getName(),
        $response->getId());

    return $response;
  }

  public function getAccount($account_id) {
    $account = $this->session->service->accounts->get(
        $this->session->merchantId, $account_id);
    printf("Retrieved account %s: '%s'\n", $account->getId(),
        $account->getName());
  }

  public function updateAccount(
      Google_Service_ShoppingContent_Account $account) {
    $original = $account->getName();

    $account->setName('updated example account');

    $response = $this->session->service->accounts->update(
        $this->session->merchantId, $account->getId(), $account);

    printf("Account name changed from '%s' to '%s'\n", $original,
        $response->getName());
  }

  public function deleteAccount($account_id) {
    // The response for a successful delete is empty
    $this->session->service->accounts->delete(
        $this->session->merchantId, $account_id);
    print "Test account deleted\n";
  }

  public function insertAccountBatch($accounts) {
    $entries = [];

    foreach ($accounts as $key => $account) {
      $entry =
          new Google_Service_ShoppingContent_AccountsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setAccount($account);
      $entry->setMerchantId($this->session->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_AccountsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponse =
        $this->session->service->accounts->custombatch($batchRequest);

    printf("Inserted %d accounts.\n", count($batchResponse->entries));

    $ids = [];

    foreach ($batchResponse->entries as $entry) {
      if (!empty($entry->getErrors())) {
        printf("There was an error inserting an account: %s\n",
            $entry->getErrors()[0]->getMessage());
      } else {
        $account = $entry->getAccount();
        printf("Inserted account %s\n", $account->getId());
        $ids[] = $account->getId();
      }
    }

    return $ids;
  }

  public function listAccounts() {
    // We set the maximum number of results to be lower than the number of
    // accounts that we inserted, to demonstrate paging
    $parameters = ['maxResults' => self::BATCH_SIZE - 1];
    // You can fetch all accounts in a loop
    do {
      $accounts = $this->session->service->accounts->listAccounts(
          $this->session->merchantId, $parameters);

      foreach ($accounts->getResources() as $account) {
        printf("%s %s\n", $account->getId(), $account->getName());
      }

      // If the result has a nextPageToken property then there are more pages
      // available to fetch
      if ($accounts->nextPageToken != null) {
        // You can fetch the next page of results by setting the pageToken
        // parameter with the value of nextPageToken from the previous result.
        $parameters['pageToken'] = $accounts->nextPageToken;
      } else {
        break;
      }
    } while(true);
  }

  public function deleteAccountBatch($ids) {
    $entries = [];

    foreach ($ids as $key => $id) {
      $entry =
          new Google_Service_ShoppingContent_AccountsCustomBatchRequestEntry();
      $entry->setMethod('delete');
      $entry->setBatchId($key);
      $entry->setAccountId($id);
      $entry->setMerchantId($this->session->merchantId);

      $entries[] = $entry;
    }

    $batchRequest =
        new Google_Service_ShoppingContent_AccountsCustomBatchRequest();
    $batchRequest->setEntries($entries);

    $batchResponses =
        $this->session->service->accounts->custombatch($batchRequest);

    print "Requested delete of batch inserted test accounts...\n";
    $entryErrors = 0;
    foreach ($batchResponses->entries as $entry) {
      $errors = $entry->getErrors();
      if (!empty($errors)) {
        $entryErrors++;
        printf("Got %d error(s) while executing batch entry %d:\n",
            count($errors), $entry->getBatchId());
        foreach ($errors as $error) {
          printf(" - [%s] %s\n", $error->getReason(), $error->getMessage());
        }
      }
    }
    if ($entryErrors !== 0) {
      throw new RuntimeException(
          sprintf('Had errors for %d batch entries when deleting accounts',
              $entryErrors));
    } else {
      print "Accounts deleted without errors.\n";
    }
  }

  private function createExampleAccounts($names) {
    $accounts = [];

    foreach ($names as $name) {
      $accounts[] = $this->createExampleAccount($name);
    }

    return $accounts;
  }

  private function createExampleAccount($name) {
    $account = new Google_Service_ShoppingContent_Account();

    $account->setName($name);
    $account->setWebsiteUrl('https://' . $name . '.example.com/');

    return $account;
  }
}
