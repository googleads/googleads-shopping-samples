<?php
/**
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

require_once __DIR__ . '/../../../vendor/autoload.php';
require_once __DIR__ . '/../../Authentication/Authentication.php';
require_once __DIR__ . '/../../Authentication/Config.php';

use Google\ApiCore\ApiException;
use Google\ApiCore\PagedListResponse;

use Google\Shopping\Merchant\Products\V1beta\ListProductsRequest;
use Google\Shopping\Merchant\Products\V1beta\Product;
use Google\Shopping\Merchant\Products\V1beta\Client\ProductsServiceClient;

// [START list_products]
/**
 * This class demonstrates how list all products in your Merchant Center account.
 */
class ListProducts
{

    /**
     * A helper function to create the parent string.
     *
     * @param array $accountId
     *      The account that owns the product.
     *
     * @return string The parent has the format: `accounts/{account_id}`
     */
    private static function getParent($accountId)
    {
        return sprintf("accounts/%s", $accountId);
    }

    /**
     * Lists all products in your Merchant Center account.
     *
     * @param array $config
     *      The configuration data used for authentication and getting the acccount
     *      ID.
     */
    public static function listProductsSample($config)
    {
        // Obtains OAuth token based on the user's configuration.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $productsServiceClient = new ProductsServiceClient($options);

        // Creates parent to identify the account from which to list all products.
        $parent = self::getParent($config['accountId']);

        // Creates the request.
        $request = new ListProductsRequest(['parent' => $parent]);

        // Calls the API and catches and prints any network failures/errors.
        try {

            // Page size is set to the default value. If you are returned more
            // responses than your page size, this code will automatically
            // re-call the service with the `pageToken` until all responses
            // are returned.
            $parameters = ['pageSize' => 25000];

            echo "Sending list products request:\n";
            /**
             * Call the listProducts service and get back a response object
             * with all products.
             *
             * @var PagedListResponse $response
             */
            $response = $productsServiceClient->listProducts($request, $parameters);

            /**
             * Loop through every product and print out its data.
             *
             * @var Product $product
             */
            foreach ($response as $product) {
                // The product includes the `productStatus` field that shows approval
                // and disapproval information.
                printf(
                    'Product data: %s%s',
                    $product->serializeToJsonString(), PHP_EOL
                );
            }
        } catch (ApiException $e) {
            printf('Call failed with message: %s%s', $e->getMessage(), PHP_EOL);
        }
    }

    /**
     * Helper to execute the sample.
     *
     * @return void
     */
    public function callSample(): void
    {
        $config = Config::generateConfig();
        self::listProductsSample($config);
    }
}
// [END list_products]

// Run the script
$sample = new ListProducts();
$sample->callSample();
