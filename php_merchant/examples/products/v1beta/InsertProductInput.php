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


use Google\Shopping\Merchant\Products\V1beta\Attributes;
use Google\Shopping\Merchant\Products\V1beta\InsertProductInputRequest;
use Google\Shopping\Merchant\Products\V1beta\ProductInput;
use Google\Shopping\Merchant\Products\V1beta\Client\ProductInputsServiceClient;
use Google\Shopping\Merchant\Products\V1beta\Shipping;
use Google\Shopping\Type\Channel\ChannelEnum;
use Google\Shopping\Type\Price;

// [START insert_product_input]
/**
 * Uploads a product input to your Merchant Center account.
 */
class InsertProductInput
{

    // ENSURE you fill in the datasource ID for the sample to work.
    private const DATASOURCE = 'INSERT_DATASOURCE_ID';

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
     * Uploads a product input to your Merchant Center account. If an input
     * with the same channel, feedLabel, contentLanguage, offerId, and dataSource
     * already exists, this method replaces that entry.
     *
     * After inserting, updating, or deleting a product input, it may take several
     * minutes before the processed product can be retrieved.
     *
     * @param array $config
     *      The configuration data used for authentication and getting the acccount
     *      ID.
     * @param string $dataSource
     *      The primary or supplemental product data source name. If the
     *      product already exists and data source provided is different, then the
     *      product will be moved to a new data source.
     *      Format: `accounts/{account}/dataSources/{datasource}`.
     *
     * @return void
     */
    public static function insertProductInputSample($config, $dataSource): void
    {

        // Gets the OAuth credentials to make the request.
        $credentials = Authentication::useServiceAccountOrTokenFile();

        // Creates options config containing credentials for the client to use.
        $options = ['credentials' => $credentials];

        // Creates a client.
        $productInputsServiceClient = new ProductInputsServiceClient($options);


        // Creates parent to identify where to insert the product.
        $parent = self::getParent($config['accountId']);

        // Calls the API and catches and prints any network failures/errors.
        try {

            // Price to be used for shipping ($33.45).
            $price = new Price(
                [
                    'amount_micros' => 33450000,
                    'currency_code' => 'USD'
                ]
            );

            $shipping = new Shipping(
                [
                    'price' => $price,
                    'country' => 'GB',
                    'service' => '1st class post'
                ]
            );

            $shipping2 = new Shipping(
                [
                    'price' => $price,
                    'country' => 'FR',
                    'service' => '1st class post'
                ]
            );

            // Creates the attributes of the product.
            $attributes = new Attributes(
                [
                    'title' => 'A Tale of Two Cities',
                    'description' => 'A classic novel about the French Revolution',
                    'link' => 'https://exampleWebsite.com/tale-of-two-cities.html',
                    'image_link' =>
                        'https://exampleWebsite.com/tale-of-two-cities.jpg',
                    'availability' => 'in stock',
                    'condition' => 'new',
                    'google_product_category' => 'Media > Books',
                    'gtin' => '9780007350896',
                    'shipping' => [$shipping, $shipping2]
                ]
            );

            // Creates the productInput with the fundamental identifiers.
            $productInput = new ProductInput(
                [
                    'channel' => ChannelEnum::ONLINE,
                    'content_language' => 'en',
                    'feed_label' => 'label',
                    'offer_id' => 'sku123ABCD',
                    'attributes' => $attributes
                ]
            );

            // Prepares the request message.
            $request = new InsertProductInputRequest(
                [
                    'parent' => $parent,
                    'data_source' => $dataSource,
                    'product_input' => $productInput
                ]
            );

            print "Sending insert ProductInput request\n";
            $response = $productInputsServiceClient->insertProductInput($request);
            print "Inserted ProductInput Name below\n";
            print $response->getName() . "\n";
            print "Inserted Product Name below\n";
            print $response->getProduct() . "\n";
        } catch (ApiException $e) {
            print $e->getMessage();
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
        // Identifies the data source that will own the product input.
        $dataSource = sprintf(
            "accounts/%s/dataSources/%s",
            $config['accountId'],
            self::DATASOURCE
        );

        // Makes the call to insert a product to the MC account.
        self::insertProductInputSample($config, $dataSource);
    }
}
// [END insert_product_input]

// Run the script
$sample = new InsertProductInput();
$sample->callSample();
