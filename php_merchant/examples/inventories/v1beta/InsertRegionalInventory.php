<?php

/**
 * Copyright 2023 Google Inc. All Rights Reserved.
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

require_once __DIR__ . '/../../../vendor/autoload.php';
require_once __DIR__ . '/../../Authentication/Authentication.php';

use Google\ApiCore\ApiException;
use Google\Shopping\Merchant\Inventories\V1beta\RegionalInventory;
use Google\Shopping\Merchant\Inventories\V1beta\Client\RegionalInventoryServiceClient;
use Google\Shopping\Merchant\Inventories\V1beta\InsertRegionalInventoryRequest;
use Google\Shopping\Type\Price;

/**
 * Class to insert a `RegionalInventory` to a given product in your
 * merchant account.
 *
 * Replaces the full `RegionalInventory` resource if an entry with the same
 * [`region`]
 * [google.shopping.merchant.inventories.v1beta.RegionalInventory.region]
 * already exists for the product.
 *
 * It might take up to 30 minutes for the new or updated `RegionalInventory`
 * resource to appear in products.
 */

// [START insert_regional_inventory]
class InsertRegionalInventory
{
    // ENSURE you fill in the merchant account and product ID for the sample to
    // work.
    private const PARENT = 'accounts/[INSERT_ACCOUNT_HERE]/products/[INSERT_PRODUCT_HERE]';
    // ENSURE you fill in region ID for the sample to work.
    private const REGIONAL_INVENTORY_REGION = 'INSERT_REGION_HERE';

    /**
     * Inserts a regional inventory underneath the parent product.
     *
     * @param string $parent The account and product where this inventory will be inserted.
     *     Format: `accounts/{account}/products/{product}`
     * @param string $regionalInventoryRegion
     *     ID of the region for this
     *     `RegionalInventory` resource. See the [Regional availability and
     *     pricing](https://support.google.com/merchants/answer/9698880) for more details.
     */
    public function insertRegionalInventorySample(string $parent, string $regionalInventoryRegion): void
    {
        // Gets the OAuth credentials to make the request.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $regionalInventoryServiceClient = new RegionalInventoryServiceClient($options);

        // Creates a price object.
        $price = new Price(
            [
                'currency_code' => "USD",
                'amount_micros' => 33450000,
            ]
        );

        // Creates a new regional inventory object.
        $regionalInventory = (new RegionalInventory())
            ->setRegion($regionalInventoryRegion)
            ->setAvailability("in stock")
            ->setPrice($price);

        $request = (new InsertRegionalInventoryRequest())
            ->setParent($parent)
            ->setRegionalInventory($regionalInventory);

        // Calls the API and catches and prints any network failures/errors.
        try {
            /** @var RegionalInventory $response */
            $response = $regionalInventoryServiceClient->insertRegionalInventory($request);
            printf('Response data: %s%s', $response->serializeToJsonString(), PHP_EOL);
        } catch (ApiException $ex) {
            printf('Call failed with message: %s%s', $ex->getMessage(), PHP_EOL);
        }
    }

    /**
     * Helper to execute the sample.
     */
    public function callSample(): void
    {
        // Makes the call to insert the regional inventory to the parent product
        // for the given region.
        $this->insertRegionalInventorySample($this::PARENT, $this::REGIONAL_INVENTORY_REGION);
    }

}
// [END insert_regional_inventory]

$sample = new InsertRegionalInventory();
$sample->callSample();
