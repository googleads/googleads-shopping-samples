<?php

require_once 'BaseSample.php';

class MultiClientAccountsSample extends BaseSample {
  // This constant defines how many example accounts to create in a batch
  const BATCH_SIZE = 10;
  const NOT_AN_MCA = "%d is not a multi-client account";

  public function run() {
    // Detect if this account is a multi-client account
    try {
      // Trying to list subaccounts for a non-MCA will cause an exception
      $this->service->accounts->listAccounts($this->merchant_id);
    } catch (Google_Service_Exception $exception) {
      $errors = $exception->getErrors();
      if (!empty($errors)
          && $errors[0]["message"] === sprintf(NOT_AN_MCA, $this->merchant_id))
          {
        print "This example requires a multi-client account.\n";
        return;
      } else {
        // Some other exception, we should rethrow so as not to hide it.
        throw $exception;
      }
    }

    $example_account_name = 'account123';
    $example_account_batch_names = array();

    for ($i = 0; $i < self::BATCH_SIZE; $i++) {
      $example_account_batch_names[] = 'account' . $i;
    }

    $example_account = $this->createExampleAccount($example_account_name);
    $example_account = $this->insertAccount($example_account);
    $example_account_id = $example_account->getId();

    $example_account_batch =
        $this->createExampleAccounts($example_account_batch_names);
    $example_account_batch_ids =
        $this->insertAccountBatch($example_account_batch);

    $this->listAccounts();

    // There is a short period after creating an account during which the
    // account can be listed with the listAccounts method but is not yet able to
    // be retrieved or modified directly. For the purposes of this example we
    // wish to create an account and then retrieve, update and delete it
    // immediately, something you would not normally do.
    //
    // To achieve this we retry each method up to 5 times with an exponential
    // back-off.
    $this->retry("getAccount", $example_account_id);
    $this->retry("updateAccount", $example_account);
    $this->retry("deleteAccount", $example_account_id);

    $this->deleteAccountBatch($example_account_batch_ids);
  }

  public function insertAccount(
      Google_Service_ShoppingContent_Account $account) {
    $response = $this->service->accounts->insert($this->merchant_id, $account);

    printf("Created a new account, '%s', with ID %d\n", $response->getName(),
        $response->getId());

    return $response;
  }

  public function getAccount($account_id) {
    $account = $this->service->accounts->get($this->merchant_id, $account_id);
    printf("Retrieved account %s: '%s'\n", $account->getId(),
        $account->getName());
  }

  public function updateAccount(
      Google_Service_ShoppingContent_Account $account) {
    $original = $account->getName();

    $account->setName('updated example account');

    $response = $this->service->accounts->update($this->merchant_id,
        $account->getId(), $account);

    printf("Account name changed from '%s' to '%s'\n", $original,
        $response->getName());
  }

  public function deleteAccount($account_id) {
    // The response for a successful delete is empty
    $this->service->accounts->delete($this->merchant_id, $account_id);
    print "Test account deleted\n";
  }

  public function insertAccountBatch($accounts) {
    $entries = array();

    foreach ($accounts as $key => $account) {
      $entry =
          new Google_Service_ShoppingContent_AccountsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setAccount($account);
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_AccountsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_response = $this->service->accounts->custombatch($batch_request);

    printf("Inserted %d accounts.\n", count($batch_response->entries));

    $ids = array();

    foreach ($batch_response->entries as $entry) {
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
    $parameters = array('maxResults' => self::BATCH_SIZE - 1);
    $accounts = $this->service->accounts->listAccounts($this->merchant_id,
        $parameters);
    // You can fetch all accounts in a loop
    while (true) {
      $accounts = $this->service->accounts->listAccounts($this->merchant_id,
          $parameters);

      foreach ($accounts->getResources() as $account) {
        printf("%s %s\n", $account->getId(), $account->getName());
      }

      // If the result has a nextPageToken property then there are more pages
      // available to fetch
      if (isset($accounts->nextPageToken)) {
        // You can fetch the next page of results by setting the pageToken
        // parameter with the value of nextPageToken from the previous result.
        $parameters["pageToken"] = $accounts->nextPageToken;
      } else {
        break;
      }
    }
  }

  public function deleteAccountBatch($ids) {
    $entries = array();

    foreach ($ids as $key => $id) {
      $entry =
          new Google_Service_ShoppingContent_AccountsCustomBatchRequestEntry();
      $entry->setMethod('delete');
      $entry->setBatchId($key);
      $entry->setAccountId($id);
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_AccountsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_responses = $this->service->accounts->custombatch($batch_request);

    $errors = 0;
    foreach ($batch_responses->entries as $entry) {
      if (!empty($entry->getErrors())) {
        $errors++;
      }
    }
    print "Requested delete of batch inserted test accounts\n";
    printf("There were %d errors\n", $errors);
  }

  private function createExampleAccounts($names) {
    $accounts = array();

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

$sample = new MultiClientAccountsSample();
$sample->run();
