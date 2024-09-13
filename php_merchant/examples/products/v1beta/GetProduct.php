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

use Google\Shopping\Merchant\Products\V1beta\GetProductRequest;
use Google\Shopping\Merchant\Products\V1beta\Product;
use Google\Shopping\Merchant\Products\V1beta\Client\ProductsServiceClient;

// [START get_product]
/**
 * This class demonstrates how to get a single product for a given Merchant Center
 * account.
 */
class GetProduct
{

    // ENSURE you fill in the product ID for the sample to work.
    private const PRODUCT = 'INSERT_PRODUCT_ID_HERE';

    /**
     * Gets a product from your Merchant Center account.
     *
     * @param array  $config
     *      The configuration data used for authentication and getting the acccount
     *      ID.
     * @param string $product
     *      The product ID to get.
     *      Format: `accounts/{account}/products/{productId}`.
     *
     * @return void
     */
    public static function getProductSample($config, $product) : void
    {
        // Gets the OAuth credentials to make the request.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $productsServiceClient = new ProductsServiceClient($options);

        try {
            // Creates the request.
            $request = new GetProductRequest(
                [
                    'name' => $product
                ]
            );

            // Sends the request.
            echo "Sending get Product request\n";
            $response = $productsServiceClient->getProduct($request);

            // Outputs the response.
            echo "Retrieved Product below\n";
            print_r($response);
        } catch (Exception $e) {
            echo $e->getMessage();
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
        // Identifies the product to retrieve.
        // The name has the format: accounts/{account}/products/{productId}.
        $product = ProductsServiceClient::productName(
            $config['accountId'],
            self::PRODUCT
        );

        self::getProductSample($config, $product);
    }
    // [END get_product]
}

$sample = new GetProduct();
$sample->callSample();
