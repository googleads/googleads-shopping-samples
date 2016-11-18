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

/**
 * Class for running through some example interactions with the
 * Accounttax service. Provides some public methods that
 * can be used to make your own calls if desired.
 */
class AccounttaxSample extends BaseSample {

  public function run() {
    printf("Retrieving tax settings for %s\n", $this->merchantId);
    $oldSettings = $this->getAccounttax($this->merchantId);
    $this->printAccounttax($oldSettings);
    printf("Updating tax settings for %s\n", $this->merchantId);
    $newSettings = $this->createTaxSample($this->merchantId);
    $this->updateAccounttax($this->merchantId, $newSettings);
    $this->printAccounttax($this->getAccounttax($this->merchantId));
    printf("Replacing old tax settings for %s\n", $this->merchantId);
    $this->updateAccounttax($this->merchantId, $oldSettings);
    $this->printAccounttax($this->getAccounttax($this->merchantId));
  }

  /**
   * Retrieves the tax settings for {@code $accountId}.  If
   * the configuration specifies a non-multi-client account, then the caller
   * can only retrieve the own information for that account, else they can
   * ask for the information of subaccounts.
   *
   * @param int $accountId the Merchant Center ID of the account for which
   *     to retrieve information
   * @return Accounttax the Accounttax resource that contains
   *     the tax information for the requested account
   * @throws InvalidArgumentException if a non-MCA requests information for
   *     a different account
   */
  public function getAccounttax($accountId) {
    if ($accountId != $this->merchantId) {
      $this->mustBeMCA('Non-multi-client accounts can only get their own '
          . 'information.');
    }
    $settings = $this->service->accounttax->get($this->merchantId, $accountId);
    return $settings;
  }

  /**
   * Updates the tax settings for {@code $accountId} using the
   * information in {@code $settings}. If the configuration specifies
   * a non-multi-client account, then the caller can only update that
   * account's information, else they can update the information of subaccounts.
   *
   * @param int $accountId the Merchant Center ID of the account for which
   *     to update information
   * @param AccountTax $settings the tax settings with which to update the
   *     account
   * @throws InvalidArgumentException if a non-MCA requests information for
   *     a different account
   */
  public function updateAccounttax($accountId, $settings) {
    if ($accountId != $this->merchantId) {
      $this->mustBeMCA('Non-multi-client accounts can only set their own '
          . 'information.');
    }
    $this->service->accounttax->update(
        $this->merchantId, $accountId, $settings);
  }

  /**
   * Prints the shipping information contained in {@code $settings}.
   *
   * @param AccountTax $settings the tax settings to print
   */
  public function printAccounttax($settings) {
    printf("Information for account %s:\n", $settings->getAccountId());
    if (empty($settings->getRules())) {
      print "- No tax rules defined.\n";
    } else {
      printf("- There are %d tax rules:\n", count($settings->getRules()));
      foreach ($settings->getRules() as $rule) {
        printf('  - For location %d in country %s: ',
            $rule->getLocationId(), $rule->getCountry());
        if ($rule->getRatePercent() !== null) {
          printf("rate is set to %s%%.\n", $rule->getRatePercent());
        }
        if ($rule->getUseGlobalRate()) {
          printf("using global tax table rate.\n");
        }
        if ($rule->getShippingTaxed()) {
          printf(" Note: shipping charges are also taxed.\n");
        }
      }
    }
  }

  private function createTaxSample($accountId) {
    $ruleNY = new Google_Service_ShoppingContent_AccountTaxTaxRule();
    $ruleNY->setCountry('US');
    // NY State, from
    // https://developers.google.com/adwords/api/docs/appendix/geotargeting
    $ruleNY->setLocationId(21167);
    $ruleNY->setUseGlobalRate(true);

    $settings = new Google_Service_ShoppingContent_AccountTax();
    $settings->setAccountId($accountId);
    $settings->setRules([$ruleNY]);

    return $settings;
  }
}

$sample = new AccounttaxSample();
$sample->run();
