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
use Google\Auth\CredentialsLoader;
use Google\Shopping\Merchant\Products\V1beta\Client\ProductInputsServiceClient;
use Google\Shopping\Merchant\Products\V1beta\DeleteProductInputRequest;
use Google\Shopping\Merchant\Products\V1beta\ProductInputName;

// [START delete_product_input]
/**
 * This class demonstrates how to delete a productinput for a given Merchant Center
 * account.
 */

class DeleteProductInput
{

    // ENSURE you fill in the product and datasource ID for the sample to work.
    // An ID assigned to a product by Google:
    // In the format `channel~contentLanguage~feedLabel~offerId`
    private const PRODUCT = 'INSERT_PRODUCT_ID_HERE';
    private const DATASOURCE = 'INSERT_DATASOURCE_ID_HERE';

    /**
     * Deletes a product input to your Merchant Center account.
     *
     * @param array  $config
     *      The configuration data used for authentication and getting the
     *      acccount ID.
     * @param string $product
     *      The product ID to delete.
     *      Format: `accounts/{account}/products/{productId}`.
     * @param string $dataSource
     *      The primary or supplemental product data source name that owns the
     *      product.
     *      Format: `accounts/{account}/dataSources/{datasource}`.
     *
     * @return void
     */
    public static function deleteProductInputSample(
        $config, $product, $dataSource
    ): void {
        // Obtains OAuth token based on the user's configuration.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates service settings using the credentials retrieved above.
        $options = ['credentials' => $credentials];

        $productInputsServiceClient = new ProductInputsServiceClient($options);

        // Calls the API and catches and prints any network failures/errors.
        try {

            $request = new DeleteProductInputRequest(
                [
                    'name' => $product,
                    'data_source' => $dataSource
                ]
            );

            echo "Sending deleteProductInput request\n";
            $productInputsServiceClient->deleteProductInput($request);
            echo "Delete successful, note that it may take a few minutes for the "
            . "delete to update in the system. If you make a products.get or "
            . "products.list request before a few  minutes have passed, the old "
            . "product data may be returned.\n";
        } catch (ApiException $e) {
            echo "An error has occurred: \n";
            echo $e->getMessage() . "\n";
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

        // The productID variable is defined at the top of the file.
        $product = ProductInputsServiceClient::productInputName(
            $config['accountId'],
            self::PRODUCT
        );

        // The name of the dataSource from which to delete the product.
        $dataSource = sprintf(
            'accounts/%s/dataSources/%s',
            $config['accountId'],
            self::DATASOURCE
        );
        self::deleteProductInputSample($config, $product, $dataSource);
    }
}
// [END delete_product_input]

// Run the script.
$sample = new DeleteProductInput();
$sample->callSample();
