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
// Accounts service, specifically those that can be performed by
// normal accounts.
class PrimaryAccountsSample extends BaseSample {
  public function run() {
    $account = $this->getAccount($this->merchantId);

    // The user that you add to an account must be a valid Google account.
    // Because this example email address does not belong to any account, the
    // call will always fail. If you wish to test it with a real account be
    // aware that a Google account can only be associated with one Merchant
    // Center account, so the call may still fail.
    if($this->config->accountSampleUser) {
      $this->addUser($account, $this->config->accountSampleUser);
      $this->removeUser($account, $this->config->accountSampleUser);
    }

    // You may see an AdWords account ID written as '123-456-7890'. However, the
    // Content API expects to recive a long integer like 1234567890. Simply
    // remove the dashes and convert to an integer.
    if($this->config->accountSampleAdWordsCID) {
      $this->linkAdWordsAccount($account,
          $this->config->accountSampleAdWordsCID);
      $this->unlinkAdWordsAccount($account,
          $this->config->accountSampleAdWordsCID);
    }
  }

  public function getAccount($accountId) {
    return $this->service->accounts->get($this->merchantId, $accountId);
  }

  public function addUser(Google_Service_ShoppingContent_Account $account,
      $email) {
    $user = new Google_Service_ShoppingContent_AccountUser();
    $user->setEmailAddress($email);
    $user->setAdmin(false); // Creating a regular, non-admin user

    $users = $account->getUsers();
    $users[] = $user;
    $account->setUsers($users);

    try {
      $response = $this->service->accounts->update($this->merchantId,
          $this->merchantId, $account);
      printf("Added user '%s' to account\n", $email);
    } catch (Google_Service_Exception $exception) {
      print ("There were errors while trying to add a user:\n");
      foreach ($exception->getErrors() as $error) {
        printf("* %s\n", $error["message"]);
      }
    }
  }

  public function removeUser(Google_Service_ShoppingContent_Account $account,
      $email) {
    $users = [];
    $found = false;

    foreach ($account->getUsers() as $user) {
      if ($user->getEmailAddress() == $email) {
        $found = true;
      } else {
        $users[] = $user;
      }
    }

    // Don't send an update request if the user we are trying to
    // remove was not found on the account.
    if ($found) {
      $account->setUsers($users);

      try {
        $response = $this->service->accounts->update($this->merchantId,
            $this->merchantId, $account);
        printf("Removed user '%s' from account\n", $email);
      } catch (Google_Service_Exception $exception) {
        print ("There were errors while trying to remove a user:\n");
        foreach ($exception->getErrors() as $error) {
          printf("* %s\n", $error["message"]);
        }
      }
    }
  }

  public function linkAdWordsAccount($account, $adwordsID) {
    $adwordsLink = new Google_Service_ShoppingContent_AccountAdwordsLink();
    $adwordsLink->setAdwordsId($adwordsID);
    $adwordsLink->setStatus('active');

    $adwordsLinks = $account->getAdwordsLinks();
    $adwordsLinks[] = $adwordsLink;
    $account->setAdwordsLinks($adwordsLinks);

    try {
      $response = $this->service->accounts->update($this->merchantId,
          $this->merchantId, $account);
      printf("Linked AdWords account '%s' to Merchant Center account\n",
          $adwordsID);
    } catch (Google_Service_Exception $exception) {
      print ("There were errors while trying to link an account:\n");
      foreach ($exception->getErrors() as $error) {
        printf("* %s\n", $error["message"]);
      }
      $account->setAdwordLinks($adwordsLinks);
    }
  }

  public function unlinkAdWordsAccount(
    Google_Service_ShoppingContent_Account $account, $adwordsID) {
    $originalLinks = $account->getAdWordsLinks();

    $adwordsLinks = array_filter($originalLinks(),
        function ($link) { return $link->getAdWordsId() != $adwordsID; })

    // Don't send an update request if the link we are trying to
    // remove was not found on the account.
    if (count($adwordsLinks) != count($originalLinks())) {
      $account->setAdwordsLinks($adwordsLinks);

      try {
        $response = $this->service->accounts->update($this->merchantId,
            $this->merchantId, $account);
        printf("Unlinked AdWords account '%s' from account\n", $adwordsID);
      } catch (Google_Service_Exception $exception) {
        print ("There were errors while trying to unlink an account:\n");
        foreach ($exception->getErrors() as $error) {
          printf("* %s\n", $error["message"]);
        }
      }
    }
  }
}

$sample = new PrimaryAccountsSample();
$sample->run();
