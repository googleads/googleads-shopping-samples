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

require_once __DIR__ . '/../../../vendor/autoload.php';
require_once __DIR__ . '/../../Authentication/Authentication.php';

use Google\ApiCore\ApiException;
use Google\Shopping\Merchant\Inventories\V1beta\Client\LocalInventoryServiceClient;
use Google\Shopping\Merchant\Inventories\V1beta\DeleteLocalInventoryRequest;

/**
 * Deletes the specified `LocalInventory` resource from the given product
 * in your merchant account.  It might take up to an hour for the
 * `LocalInventory` to be deleted from the specific product.
 * Once you have received a successful delete response, wait for that
 * period before attempting a delete again.
 */

// [START delete_local_inventory]
class DeleteLocalInventory
{

    // ENSURE you fill in the merchant account, product, and region ID for the
    // sample to work.
    private const ACCOUNT = 'INSERT_ACCOUNT_ID_HERE';
    private const PRODUCT = 'INSERT_PRODUCT_ID_HERE';
    private const STORE_CODE = 'INSERT_STORE_CODE_HERE';

    /**
     * Deletes a specific local inventory of a given product.
     *
     * @param string $formattedName The name of the `LocalInventory` resource
     * to delete.
     *     Format: `accounts/{account}/products/{product}/localInventories/{store_code}`
     *     Please see {@see LocalInventoryServiceClient::localInventoryName()}
     *     for help formatting this field.
     */
    function deleteLocalInventorySample(string $formattedName): void
    {
         // Gets the OAuth credentials to make the request.
         $credentials = Authentication::useServiceAccountOrTokenFile();

         // Creates options config containing credentials for the client to use.
         $options = ['credentials' => $credentials];

         // Creates a client.
         $localInventoryServiceClient = new LocalInventoryServiceClient($options);

        // Prepare the request message.
        $request = (new DeleteLocalInventoryRequest())
            ->setName($formattedName);

         // Calls the API and catches and prints any network failures/errors.
         try {
             $localInventoryServiceClient->deleteLocalInventory($request);
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
        $formattedName = LocalInventoryServiceClient::localInventoryName(
            $this::ACCOUNT,
            $this::PRODUCT,
            $this::STORE_CODE
        );

        // Deletes the specific local inventory of the parent product.
        $this->deleteLocalInventorySample($formattedName);
    }
}
// [END delete_local_inventory]

$sample = new DeleteLocalInventory();
$sample->callSample();