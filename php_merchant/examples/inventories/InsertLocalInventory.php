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

require_once __DIR__ . '/../../vendor/autoload.php';
require_once __DIR__ . '/../Authentication/Authentication.php';

use Google\ApiCore\ApiException;
use Google\Shopping\Merchant\Inventories\V1beta\LocalInventory;
use Google\Shopping\Merchant\Inventories\V1beta\Client\LocalInventoryServiceClient;
use Google\Shopping\Merchant\Inventories\V1beta\InsertLocalInventoryRequest;
use Google\Shopping\Type\Price;

/**
 * Class to insert a `LocalInventory` to a given product in your
 * merchant account.
 *
 * Replaces the full `LocalInventory` resource if an entry with the same
 * [`storeCode`]
 * [google.shopping.merchant.inventories.v1beta.LocalInventory.storeCode]
 * already exists for the product.
 *
 * It might take up to 30 minutes for the new or updated `LocalInventory`
 * resource to appear in products.
 */

// [START insert_local_inventory]
class InsertLocalInventory
{
    // ENSURE you fill in the merchant account and product ID for the sample to
    // work.
    private const PARENT = 'accounts/[INSERT_ACCOUNT_HERE]/products/[INSERT_PRODUCT_HERE]';
    // ENSURE you fill in store code for the sample to work.
    private const LOCAL_INVENTORY_STORE_CODE = 'INSERT_STORE_CODE_HERE';

    /**
     * Inserts a local inventory underneath the parent product.
     *
     * @param string $parent The account and product where this inventory will be inserted.
     *     Format: `accounts/{account}/products/{product}`
     * @param string $localInventoryRegion
     *     ID of the region for this
     *     `LocalInventory` resource. See the [Local availability and
     *     pricing](https://support.google.com/merchants/answer/9698880) for more details.
     */
    public function insertLocalInventorySample(
        string $parent,
        string $localInventoryStoreCode
    ): void {
        // Gets the OAuth credentials to make the request.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $localInventoryServiceClient = new LocalInventoryServiceClient($options);

        // Creates a price object.
        $price = new Price(
            [
                'currency_code' => "USD",
                'amount_micros' => 33450000,
            ]
        );

        // Creates a new local inventory object.
        $localInventory = (new LocalInventory())
            ->setStoreCode($localInventoryStoreCode)
            ->setAvailability("in stock")
            ->setPrice($price);

        $request = (new InsertLocalInventoryRequest())
            ->setParent($parent)
            ->setLocalInventory($localInventory);

        // Calls the API and catches and prints any network failures/errors.
        try {
            /** @var LocalInventory $response */
            $response = $localInventoryServiceClient->insertLocalInventory($request);
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
        // Makes the call to insert the local inventory to the parent product
        // for the given region.
        $this->insertLocalInventorySample($this::PARENT, $this::LOCAL_INVENTORY_STORE_CODE);
    }

}
// [END insert_local_inventory]

$sample = new InsertLocalInventory();
$sample->callSample();
