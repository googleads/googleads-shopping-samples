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
 * Shippingsettings service. Provides some public methods that
 * can be used to make your own calls if desired.
 */
class ShippingsettingsSample extends BaseSample {

  public function run() {
    printf("Retrieving shipping settings for %s\n", $this->merchantId);
    $oldSettings = $this->getShippingsettings($this->merchantId);
    $this->printShippingsettings($oldSettings);
    printf("Updating shipping settings for %s\n", $this->merchantId);
    $newSettings = $this->createShippingSample();
    $this->updateShippingsettings($this->merchantId, $newSettings);
    $this->printShippingsettings($this->getShippingsettings($this->merchantId));
    printf("Replacing old shipping settings for %s\n", $this->merchantId);
    $this->updateShippingsettings($this->merchantId, $oldSettings);
    $this->printShippingsettings($this->getShippingsettings($this->merchantId));
  }

  /**
   * Retrieves the shipping settings for {@code $accountId}. If
   * the configuration specifies a non-multi-client account, then the caller
   * can only retrieve the own information for that account, else they can
   * ask for the information of subaccounts.
   *
   * @param int $accountId the Merchant Center ID of the account for which
   *     to retrieve information
   * @return Shippingsettings the Shippingsettings resource that contains
   *     the shipping information for the requested account
   * @throws InvalidArgumentException if a non-MCA requests information for
   *     a different account
   */
  public function getShippingsettings($accountId) {
    if ($accountId != $this->merchantId) {
      $this->mustBeMCA('Non-multi-client accounts can only get their own '
          . 'information.');
    }
    $settings = $this->service->shippingsettings->get(
        $this->merchantId, $accountId);
    return $settings;
  }

  /**
   * Updates the shipping settings for {@code $accountId} using the
   * information in {@code $settings}.  If the configuration specifies
   * a non-multi-client account, then the caller can only update that
   * account's information, else they can update the information of subaccounts.
   *
   * @param int $accountId the Merchant Center ID of the account for which
   *     to retrieve information
   * @param Shippingsettings $settings the Shippingsettings resource with
   *     which to update the account
   * @throws InvalidArgumentException if a non-MCA requests information for
   *     a different account
   */
  public function updateShippingsettings($accountId, $settings) {
    if ($accountId != $this->merchantId) {
      $this->mustBeMCA('Non-multi-client accounts can only set their own '
          . 'information.');
      return;
    }
    $this->service->shippingsettings->update(
        $this->merchantId, $accountId, $settings);
  }

  /**
   * Prints the shipping information contained in {@code $settings}.
   *
   * @param Shippingsettings $settings the Shippingsettings resource to print
   */
  public function printShippingsettings($settings) {
    printf("Information for account %s:\n", $settings->getAccountId());
    if (empty($settings->getPostalCodeGroups())) {
      print "- No postal code groups defined.\n";
    } else {
      printf("- There are %d postal code group(s):\n",
          count($settings->getPostalCodeGroups()));
      foreach ($settings->getPostalCodeGroups() as $group) {
        printf("  Postal group "%s":\n", $group->getName());
        printf("  - Country: %s\n", $group->getCountry());
        printf("  - Contains %d postal code range(s).\n",
            count($group->getPostalCodeRanges()));
      }
    }
    if (empty($settings->getServices())) {
      print "- No shipping services defined.\n";
    } else {
      printf("- There are %d shipping service(s):\n",
          count($settings->getServices()));
      foreach ($settings->getServices() as $service) {
        printf("  Service "%s":\n", $service->getName());
        printf("  - Active: %s\n", $service->getActive() ? 'yes' : 'no');
        printf("  - Country: %s\n", $service->getDeliveryCountry());
        printf("  - Currency: %s\n", $service->getCurrency());
        printf("  - Delivery time: %d - %d days\n",
          $service->getDeliveryTime()->getMinTransitTimeInDays(),
          $service->getDeliveryTime()->getMaxTransitTimeInDays());
        printf("  - Contains %d rate group(s).\n",
          count($service->getRateGroups()));
      }
    }
  }

  private function createShippingSample() {
    $deliveryTime = new Google_Service_ShoppingContent_DeliveryTime();
    $deliveryTime->setMinTransitTimeInDays(3);
    $deliveryTime->setMaxTransitTimeInDays(7);

    $price = new Google_Service_ShoppingContent_Price();
    $price->setValue(5.00);
    $price->setCurrency('USD');

    $value = new Google_Service_ShoppingContent_Value();
    $value->setFlatRate($price);

    $rateGroup = new Google_Service_ShoppingContent_RateGroup();
    $rateGroup->setSingleValue($value);

    $service = new Google_Service_ShoppingContent_Service();
    $service->setName('USPS');
    $service->setActive(true);
    $service->setDeliveryCountry('US');
    $service->setCurrency('USD');
    $service->setDeliveryTime($deliveryTime);
    $service->setRateGroups([$rateGroup]);

    $settings = new Google_Service_ShoppingContent_ShippingSettings();
    $settings->setPostalCodeGroups([]);
    $settings->setServices([$service]);

    return $settings;
  }
}

$sample = new ShippingsettingsSample();
$sample->run();
