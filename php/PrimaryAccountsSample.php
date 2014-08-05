<?php

require_once 'BaseSample.php';

class PrimaryAccountsSample extends BaseSample {
  public function run() {
    $account = $this->getAccount($this->merchant_id);

    // The user that you add to an account must be a valid Google account.
    // Because this example email address does not belong to any account, the
    // call will always fail. If you wish to test it with a real account be
    // aware that a Google account can only be associated with one Merchant
    // Center account, so the call may still fail.
    $this->addUser($account, 'user@example.com');
    $this->removeUser($account, 'user@example.com');

    // You may see an AdWords account ID written as '123-456-7890'. However, the
    // Content API expects to recive a long integer like 1234567890. Simply
    // remove the dashes and convert to an integer.
    $this->linkAdWordsAccount($account, 1234567890);
    $this->unlinkAdWordsAccount($account, 1234567890);
  }

  public function getAccount($account_id) {
    return $this->service->accounts->get($this->merchant_id, $account_id);
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
      $response = $this->service->accounts->update($this->merchant_id,
          $this->merchant_id, $account);
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
    $users = array();
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
        $response = $this->service->accounts->update($this->merchant_id,
            $this->merchant_id, $account);
        printf("Removed user '%s' from account\n", $email);
      } catch (Google_Service_Exception $exception) {
        print ("There were errors while trying to remove a user:\n");
        foreach ($exception->getErrors() as $error) {
          printf("* %s\n", $error["message"]);
        }
      }
    }
  }

  public function linkAdWordsAccount($account, $adwords_id) {
    $adwords_link = new Google_Service_ShoppingContent_AccountAdwordsLink();
    $adwords_link->setAdwordsId($adwords_id);
    $adwords_link->setStatus('active');

    $adwords_links = $account->getAdwordsLinks();
    $adwords_links[] = $adwords_link;
    $account->setAdwordsLinks($adwords_links);

    try {
      $response = $this->service->accounts->update($this->merchant_id,
          $this->merchant_id, $account);
      printf("Linked AdWords account '%s' to Merchant Center account\n",
          $adwords_id);
    } catch (Google_Service_Exception $exception) {
      print ("There were errors while trying to link an account:\n");
      foreach ($exception->getErrors() as $error) {
        printf("* %s\n", $error["message"]);
      }
      $account->setAdwordLinks($adwords_links);
    }
  }

  public function unlinkAdWordsAccount(
      Google_Service_ShoppingContent_Account $account, $adwords_id) {
    $adwords_links = array();
    $found = false;

    foreach ($account->getAdwordsLinks() as $adwords_link) {
      if ($adwords_link->getAdwordsId() == $adwords_id) {
        $found = true;
      } else {
        $adwords_links[] = $adwords_link;
      }
    }

    // Don't send an update request if the link we are trying to
    // remove was not found on the account.
    if ($found) {
      $account->setAdwordsLinks($adwords_links);

      try {
        $response = $this->service->accounts->update($this->merchant_id,
            $this->merchant_id, $account);
        printf("Unlinked AdWords account '%s' from account\n", $adwords_id);
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
