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
    $account = $this->getAccount($this->session->merchantId);

    // The user that you add to an account must be a valid Google account.
    // Because this example email address does not belong to any account, the
    // call will always fail. If you wish to test it with a real account be
    // aware that a Google account can only be associated with one Merchant
    // Center account, so the call may still fail.
    if(array_key_exists('accountSampleUser', $this->session->config)) {
      $this->addUser($account, $this->session->config['accountSampleUser']);
      $this->removeUser($account, $this->session->config['accountSampleUser']);
    }

    // You may see an Google Ads account ID written as '123-456-7890'. However,
    // the Content API expects to recive a long integer like 1234567890. Simply
    // remove the dashes and convert to an integer.
    if(array_key_exists('accountSampleAdWordsCID', $this->session->config)) {
      $this->linkAdsAccount($account,
          $this->session->config['accountSampleAdWordsCID']);
      $this->unlinkAdsAccount($account,
          $this->session->config['accountSampleAdWordsCID']);
    }
  }

  public function getAccount($accountId) {
    return $this->session->service->accounts->get(
        $this->session->merchantId, $accountId);
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
      $response = $this->session->service->accounts->update(
          $this->session->merchantId, $this->session->merchantId, $account);
      printf("Added user '%s' to account\n", $email);
    } catch (Google_Service_Exception $exception) {
      print ("There were errors while trying to add a user:\n");
      foreach ($exception->getErrors() as $error) {
        printf("* %s\n", $error['message']);
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
        $response = $this->session->service->accounts->update(
            $this->session->merchantId, $this->session->merchantId, $account);
        printf("Removed user '%s' from account\n", $email);
      } catch (Google_Service_Exception $exception) {
        print ("There were errors while trying to remove a user:\n");
        foreach ($exception->getErrors() as $error) {
          printf("* %s\n", $error['message']);
        }
      }
    }
  }

  public function linkAdsAccount($account, $adsId) {
    $adsLink = new Google_Service_ShoppingContent_AccountAdsLink();
    $adsLink->setAdsId($adsId);
    $adsLink->setStatus('active');

    $adsLinks = $account->getAdsLinks();
    $adsLinks[] = $adsLink;
    $account->setAdsLinks($adsLinks);

    try {
      $response = $this->session->service->accounts->update(
          $this->session->merchantId, $this->session->merchantId, $account);
      printf("Linked Google Ads account '%s' to Merchant Center account\n",
             $adsId);
    } catch (Google_Service_Exception $exception) {
      print ("There were errors while trying to link a Google Ads account:\n");
      foreach ($exception->getErrors() as $error) {
        printf("* %s\n", $error['message']);
      }
      $account->setAdsLinks($adsLinks);
    }
  }

  public function unlinkAdsAccount(
    Google_Service_ShoppingContent_Account $account, $adsId) {
    $originalLinks = $account->getAdsLinks();

    $adsLinks = array_filter($originalLinks,
        function ($link) {
            return $link->getAdsId() !== $adsId;
        });

    // Don't send an update request if the link we are trying to
    // remove was not found on the account.
    if (count($adsLinks) != count($originalLinks)) {
      $account->setAdsLinks($adsLinks);

      try {
        $response = $this->session->service->accounts->update(
            $this->session->merchantId, $this->session->merchantId, $account);
        printf("Unlinked Google Ads account '%s' from account\n", $adsId);
      } catch (Google_Service_Exception $exception) {
        print ("There were errors while trying to unlink an account:\n");
        foreach ($exception->getErrors() as $error) {
          printf("* %s\n", $error['message']);
        }
      }
    }
  }
}
