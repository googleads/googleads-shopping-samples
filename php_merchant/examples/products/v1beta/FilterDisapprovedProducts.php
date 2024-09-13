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

// [START filter_disapproved_products]
/**
 * This class demonstrates how to filter through all the products returned via
 * `Products.List` to find all the disapproved products for a given Merchant Center
 * account.
 */
class FilterDisapprovedProducts
{

    /**
     * A helper function to create the parent string.
     *
     * @param array $accountId The account that owns the product.
     *
     * @return string The parent has the format: `accounts/{account_id}`
     */
    private static function getParent($accountId)
    {
        return sprintf("accounts/%s", $accountId);
    }

    /**
     * Filters to find all disapproved products in your Merchant Center account.
     *
     * @param array $config
     *      The configuration data used for authentication and getting the acccount
     *      ID.
     *
     * @return void
     */
    public static function filterDisapprovedProductsSample($config): void
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

            $disapprovedProducts = [];

            echo "Sending list products request:\n";
            echo ("Will filter through response for disapproved products.");

            /**
             * Call the listProducts service and get back a response object
             * with all products.
             *
             * @var PagedListResponse $response
             */
            $response = $productsServiceClient->listProducts($request, $parameters);

            /**
             * Filter through all the destinations and capture if the product
             * is disapproved in any country.
             *
             * @var Product $product
             */
            foreach ($response as $product) {
                $destinationStatuses = $product
                    ->getProductStatus()
                    ->getDestinationStatuses();

                foreach ($destinationStatuses as $destinationStatus) {
                    if (count($destinationStatus->getDisapprovedCountries()) > 0) {
                        $disapprovedProducts[] = $product;
                        // Exit the inner loop, so we don't add the same product
                        // multiple times if it's disapproved for different
                        // destinations.
                        break;
                    }
                }
                // The product includes the `productStatus` field that shows approval
                // and disapproval information.
                printf(
                    'Disapproved Product data: %s%s',
                    $product->serializeToJsonString(),
                    PHP_EOL
                );

            }
            echo "The following count of disapproved products were returned: ";
            echo count($disapprovedProducts) . PHP_EOL;
        } catch (ApiException $e) {
            printf('Call failed with message: %s%s', $ex->getMessage(), PHP_EOL);
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
        self::filterDisapprovedProductsSample($config);
    }
}
// [END filter_disapproved_products]

// Run the script
$sample = new FilterDisapprovedProducts();
$sample->callSample();
