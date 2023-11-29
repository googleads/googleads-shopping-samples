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

require __DIR__ . '/../../vendor/autoload.php';
require_once __DIR__ . '/Authentication.php';

/**
 * This class uses your client secrets file and walks you through the OAuth
 * flow to generate and store a refresh token on your local machine.
 */
class GenerateUserCredentials
{
    public static function main(): void
    {
        Authentication::generateUserCredentials();
    }
}

GenerateUserCredentials::main();