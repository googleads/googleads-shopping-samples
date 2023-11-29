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
require_once __DIR__ . '/Config.php';

use Google\Auth\CredentialsLoader;
use Google\Auth\OAuth2;
use Google\Auth\Credentials\UserRefreshCredentials;
use Psr\Http\Message\ServerRequestInterface;
use React\EventLoop\Loop;
use React\Http\HttpServer;
use React\Http\Message\Response;
use React\Socket\SocketServer;

/*
 * A class that provides authentication credentials to use the merchant API.
 */
class Authentication
{
    private const SCOPE = 'https://www.googleapis.com/auth/content';
    private const AUTHORIZATION_URI = 'https://accounts.google.com/o/oauth2/v2/auth';
    private const OAUTH2_CALLBACK_IP_ADDRESS = '127.0.0.1';

    /**
     * This method is called on each sample request, using either the service
     * account or stored token.json file to get valid authentication
     * credentials.
     */
    public static function useServiceAccountOrTokenFile()
    {
        $config = Config::generateConfig();

        print "Attempting to load service account information." . PHP_EOL;
        if (file_exists($config['serviceAccountFile'])) {
            print 'Service account file exists, will use service account '
                . 'to authenticate.'
                . PHP_EOL;
            return $config['serviceAccountFile'];
        } else {
            print 'Service account file does not exist, attempting to load '
                . 'token file.'
                . PHP_EOL;

            // Check if the token file exists, and if so, return the contents of the file. Ensure
            // you are running this code from the root directory of the PHP samples.
            if (file_exists($config['tokenFile'])) {
                // Read in the object of `refresh_token`, `client_secret` and
                // `client_id` and cast it to an array.
                $tokenJSON = (array) json_decode(
                    file_get_contents($config['tokenFile'])
                );

                // Create OAuth credentials to be used in the merchant api
                // client requests.
                $credentials = new UserRefreshCredentials(
                    scope: null,
                    jsonKey: $tokenJSON
                );
                print 'Token file exists, will use token file to authenticate.'
                    . PHP_EOL;
                return $credentials;
            } else {
                print 'Token file does not exist, attempting to see if client '
                    . 'secrets file exists.'
                    . PHP_EOL;

                if (file_exists($config['clientSecretsFile'])) {
                    throw new Exception(
                        'Client secrets file exists, please run the '
                            . '`GenerateUserCredentials example to use your client '
                            . 'secrets to get OAuth2 refresh token. Then re-run your '
                            . 'sample.'
                            . PHP_EOL
                    );
                } else {
                    throw new Exception(
                        'Service account file, token file, and client secrets '
                            . 'file do not exist. Please follow the instructions in '
                            . 'the top level ReadMe to create a service account or '
                            . 'client secrets file.'
                            . PHP_EOL
                    );
                }
            }
        }
    }

    /**
     * This function goes through the OAuth Flow with your client secrets
     * to generate and store a token.json file to use for authentication.
     */
    public static function generateUserCredentials(): void
    {
        $config = Config::generateConfig();

        print 'Token file does not exist, attempting to load client secrets '
            . 'file.'
            . PHP_EOL;

        if (file_exists($config['clientSecretsFile'])) {
            print 'Client secrets file exists, will use client secrets to get '
                . 'an OAuth2 refresh token.'
                . PHP_EOL;


            if (!class_exists(HttpServer::class)) {
                print 'Please install "react/http" package to be able to run '
                    . 'this example';
                    throw new Exception('Please install "react/http" package');
            }

            // Creates a socket for localhost with random port. Port 0 is used
            // to tell the SocketServer to create a server with a random port.
            $socket = new SocketServer(self::OAUTH2_CALLBACK_IP_ADDRESS . ':0');

            // Ensure you've created a client secrets file in the appropriate
            // location by following the instructions in the top level ReadMe.
            // Remember that if you are using a web application, you need to add
            // the following to its "Authorized redirect URIs": http://127.0.0.1
            // in your GCP console (https://console.cloud.google.com/).

            // Read the Client Secrets JSON file.
            $json = file_get_contents($config['clientSecretsFile']);

            // Decode the JSON file.
            $json_data = json_decode($json, true);

            $redirectUrl = str_replace('tcp:', 'http:', $socket->getAddress());
            $oauth2 = new OAuth2(
                [
                    'clientId' => $json_data['web']['client_id'],
                    'clientSecret' => $json_data['web']['client_secret'],
                    'authorizationUri' => $json_data['web']['auth_uri'],
                    'redirectUri' => $redirectUrl,
                    'tokenCredentialUri' => $json_data['web']['token_uri'],
                    'scope' => self::SCOPE,
                    // Create a 'state' token to prevent request forgery. See
                    // https://developers.google.com/identity/protocols/OpenIDConnect#createxsrftoken
                    // for details.
                    'state' => sha1(openssl_random_pseudo_bytes(1024))
                ]
            );

            $authToken = null;

            $server = new HttpServer(
                function (ServerRequestInterface $request) use ($oauth2, &$authToken, $json_data) {
                    // Stops the server after tokens are retrieved.
                    if (!is_null($authToken)) {
                        Loop::stop();
                    }

                    // Check if the requested path is the one set as the
                    // redirect URI. We add '/' here so the parse_url method
                    // can function correctly, since it cannot detect the URI
                    // without '/' at the end, which is the case for the value
                    // of getRedirectUri().
                    if (
                        $request->getUri()->getPath()
                        !== parse_url($oauth2->getRedirectUri() . '/', PHP_URL_PATH)
                    ) {
                        return new Response(
                            404,
                            ['Content-Type' => 'text/plain'],
                            'Page not found'
                        );
                    }

                    // Exit if the state is invalid to prevent request forgery.
                    $state = $request->getQueryParams()['state'];
                    if (empty($state) || ($state !== $oauth2->getState())) {
                        throw new UnexpectedValueException(
                            "The state is empty or doesn't match the expected one."
                                . PHP_EOL
                        );
                    };

                    // Set the authorization code and fetch refresh and access
                    // tokens.
                    $code = $request->getQueryParams()['code'];
                    $oauth2->setCode($code);
                    $authToken = $oauth2->fetchAuthToken();
                    $refreshToken = $authToken['refresh_token'];
                    print 'Your refresh token is: ' . $refreshToken . PHP_EOL;
                    print 'You can now run any example to automatically use '
                         . 'your new refresh token to generate an access token and '
                         . 'succesfully authenticate your request.'
                         . PHP_EOL;

                    $token_file_credentials = [
                        'client_id' => $json_data['web']['client_id'],
                        'client_secret' => $json_data['web']['client_secret'],
                        'refresh_token' => $refreshToken
                    ];

                    file_put_contents(
                        "token.json",
                        json_encode($token_file_credentials)
                    );

                    return new Response(
                        200,
                        ['Content-Type' => 'text/plain'],
                        'Your refresh token has been fetched. Check the '
                            . 'console output for further instructions.'
                    );
                }
            );

            $server->listen($socket);
            printf(
                'Log into the Google account you use for Google Ads and visit '
                    . 'the following URL in your web browser: %1$s%2$s%1$s%1$s',
                PHP_EOL,
                implode(
                    [$oauth2->buildFullAuthorizationUri(['access_type' => 'offline']), '&prompt=consent']
                )
            );
        } else {
            print 'Client secrets file does not exist. Please follow the '
                . 'instructions in the top level ReadMe to create a client secrets '
                . 'file.'
                . PHP_EOL;
        }
    }
}
