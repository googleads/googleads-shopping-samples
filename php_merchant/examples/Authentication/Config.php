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

/**
 * A configuration class to provide access to the location on the local machine
 * where the service account, and/or client sercrets and/or token file lives.
 */
class Config
{
    /**
     * Returns a Config object to be used by other classes.
     */
    public static function generateConfig()
    {
        $configPath =  join(
            DIRECTORY_SEPARATOR,
            [getenv("HOME"), 'shopping-samples']
        );

        $configDir = join(
            DIRECTORY_SEPARATOR,
            [$configPath, 'content']
        );

        $serviceAccountFile = join(
            DIRECTORY_SEPARATOR,
            [$configDir, "service-account.json"]
        );

        $clientSecretsFile = join(
            DIRECTORY_SEPARATOR,
            [$configDir, "client-secrets.json"]
        );

        $tokenFile = __DIR__ . '/../../token.json';

        $merchantInfoFile= join(
            DIRECTORY_SEPARATOR,
            [$configDir, 'merchant-info.json']
        );

        // Read the MerchantInfo JSON file.
        $jsonData = file_get_contents($merchantInfoFile);

        // Check if the file was read successfully.
        if ($jsonData === false) {
            die('Error reading JSON file');
        }

        // Decode the JSON data into a PHP associative array.
        $data = json_decode($jsonData, true);

        // Check if the JSON data was decoded successfully.
        if ($data === null) {
            die('Error decoding JSON data');
        }

        // Exit the program if the "merchantId" key doesn't exist.
        if (!isset($data['merchantId'])) {
            die('Merchant ID not found in the JSON data');
        }

        $configObject = [
            'serviceAccountFile' => $serviceAccountFile,
            'clientSecretsFile' => $clientSecretsFile,
            'tokenFile' => $tokenFile,
            'accountId' => $data['merchantId']
        ];

        return $configObject;
    }
}