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
use Google\Shopping\Merchant\Inventories\V1beta\Client\RegionalInventoryServiceClient;
use Google\Shopping\Merchant\Inventories\V1beta\DeleteRegionalInventoryRequest;

/**
 * Deletes the specified `RegionalInventory` resource from the given product
 * in your merchant account.  It might take up to an hour for the
 * `RegionalInventory` to be deleted from the specific product.
 * Once you have received a successful delete response, wait for that
 * period before attempting a delete again.
 */

// [START delete_regional_inventory]
class DeleteRegionalInventory
{

    // ENSURE you fill in the merchant account, product, and region ID for the
    // sample to work.
    private const ACCOUNT = 'INSERT_ACCOUNT_ID_HERE';
    private const PRODUCT = 'INSERT_PRODUCT_ID_HERE';
    private const REGION = 'INSERT_REGION_ID_HERE';

    /**
     * Deletes a specific regional inventory of a given product.
     *
     * @param string $formattedName The name of the `RegionalInventory` resource
     * to delete.
     *     Format: `accounts/{account}/products/{product}/regionalInventories/{region}`
     *     Please see {@see RegionalInventoryServiceClient::regionalInventoryName()}
     *     for help formatting this field.
     */
    function deleteRegionalInventorySample(string $formattedName): void
    {
         // Gets the OAuth credentials to make the request.
         $credentials = Authentication::useServiceAccountOrTokenFile();

         // Creates options config containing credentials for the client to use.
         $options = ['credentials' => $credentials];

         // Creates a client.
         $regionalInventoryServiceClient = new RegionalInventoryServiceClient($options);


         // Prepare the request message.
         $request = (new DeleteRegionalInventoryRequest())
            ->setName($formattedName);

         // Calls the API and catches and prints any network failures/errors.
         try {
             $regionalInventoryServiceClient->deleteRegionalInventory($request);
             print 'Delete call completed successfully.' . PHP_EOL;
         } catch (ApiException $ex) {
             printf('Call failed with message: %s%s', $ex->getMessage(), PHP_EOL);
         }
    }

    /**
     * Helper to execute the sample.
     */
    function callSample(): void
    {
        // These variables are defined at the top of the file.
        $formattedName = RegionalInventoryServiceClient::regionalInventoryName(
            $this::ACCOUNT,
            $this::PRODUCT,
            $this::REGION
        );

        // Deletes the specific regional inventory of the parent product.
        $this->deleteRegionalInventorySample($formattedName);
    }
}
// [END delete_regional_inventory]

$sample = new DeleteRegionalInventory();
$sample->callSample();