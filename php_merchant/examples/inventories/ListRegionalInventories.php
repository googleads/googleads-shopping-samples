<?php

/**
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
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
use Google\ApiCore\PagedListResponse;
use Google\Shopping\Merchant\Inventories\V1beta\RegionalInventory;
use Google\Shopping\Merchant\Inventories\V1beta\Client\RegionalInventoryServiceClient;
use Google\Shopping\Merchant\Inventories\V1beta\ListRegionalInventoriesRequest;

/**
 * Class to list the `RegionalInventory` resources for the given product in your
 * merchant account. The response might contain fewer items than specified by
 * `pageSize`. If `pageToken` was returned in previous request, it can be
 * used to obtain additional results.
 *
 * `RegionalInventory` resources are listed per product for a given account.
 */

// [START list_regional_inventories]
class ListRegionalInventories
{

    // ENSURE you fill in the merchant account and product ID for the sample to
    // work.
    private const PARENT = 'accounts/[INSERT_ACCOUNT_HERE]/products/[INSERT_PRODUCT_HERE]';

    /**
     * Lists all the regional inventories of a given product.
     *
     * @param string $parent The `name` of the parent product to list `RegionalInventory`
     *     resources for.
     *     Format: `accounts/{account}/products/{product}`
     */
    function listRegionalInventoriesSample(string $parent): void
    {
        // Gets the OAuth credentials to make the request.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $regionalInventoryServiceClient = new RegionalInventoryServiceClient($options);

        // Prepare the request message.
        $request = (new ListRegionalInventoriesRequest())
            ->setParent($parent);

        // Calls the API and catches and prints any network failures/errors.
        try {
            // Page size is set to the default value. If you are returned more
            // responses than your page size, this code will automatically
            // re-call the service with the `pageToken` until all responses
            // are returned.
            $parameters = ['pageSize' => 25000];

            /** @var PagedListResponse $response */
            $response =
                $regionalInventoryServiceClient->listRegionalInventories($request, $parameters);

            /** @var RegionalInventory $element */
            foreach ($response as $element) {
                printf('RegionalInventory data: %s%s', $element->serializeToJsonString(), PHP_EOL);
            }
        } catch (ApiException $ex) {
            printf('Call failed with message: %s%s', $ex->getMessage(), PHP_EOL);
        }
    }

    // Helper to execute the sample.
    function callSample(): void
    {
        // Lists all the regional inventories of the parent product.
        $this->listRegionalInventoriesSample($this::PARENT);
    }
}
// [END list_regional_inventories]

$sample = new ListRegionalInventories();
$sample->callSample();