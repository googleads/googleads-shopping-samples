/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
<?php

// Note that these samples are written to be run as a command line application,
// not as a webpage.


// This file assumes you are using Composer.  If not, adjust the
// following line appropriately.
require_once 'vendor/autoload.php';

abstract class BaseSample {
  protected $merchantId;
  protected $config;
  protected $configDir;
  protected $service;
  protected $websiteUrl;
  protected $configFile;

  const CONFIG_DIR_NAME = '.shopping-content-samples';
  const CONFIGFILE_NAME = 'merchant-info.json';
  const SERVICE_ACCOUNT_FILE_NAME = 'content-service.json';
  const OAUTH_CLIENT_FILE_NAME = 'content-oauth2.json';

  // Constructor that sets up configuration and authentication for all
  // the samples.
  public function __construct () {
    $this->configDir = $this->getHome() . '/' . self::CONFIG_DIR_NAME;
    $this->configFile = $this->configDir . '/' . self::CONFIGFILE_NAME;

    if (file_exists($this->configFile)) {
      $this->config = json_decode(file_get_contents($this->configFile));
    } else {
      throw new InvalidArgumentException(sprintf('Could not find or read the '
          . 'config file at %s. You can use the config.json file in the '
          . 'samples root as a template.', $this->configFile));
    }

    if (!$this->config) {
      throw new Exception(sprintf('The config file at %s is not valid '
          . 'JSON format. You can use the config.json file in the samples '
          . 'root as a template.', $this->configFile));
    }

    $client = new Google_Client();
    $client->setApplicationName($this->config->applicationName);
    $client->setScopes('https://www.googleapis.com/auth/content');
    $this->authenticate($client);

    $this->merchantId = $this->config->merchantId;
    $this->service = new Google_Service_ShoppingContent($client);
    $this->websiteUrl =
        $this->service->accounts->get($this->merchantId,
                                      $this->merchantId)->getWebsiteUrl;
}

  abstract public function run();

  /**
   * Attempts to find the home directory of the user running the PHP script.
   *
   * @return string The path to the home directory with any trailing directory
   *     separators removed
   * @throws UnexpectedValueException if a home directory could not be found
   */
  public function getHome() {
    $home = null;

    if (!empty(getenv('HOME'))) {
      // Try the environmental variables.
      $home = getenv('HOME');
    } else if (!empty($_SERVER['HOME'])) {
      // If not in the environment variables, check the superglobal $_SERVER as
      // a last resort.
      $home = $_SERVER['HOME'];
    } else if(!empty(getenv('HOMEDRIVE')) && !empty(getenv('HOMEPATH'))) {
      // If the 'HOME' environmental variable wasn't found, we may be on
      // Windows.
      $home = getenv('HOMEDRIVE') . getenv('HOMEPATH');
    } else if(!empty($_SERVER['HOMEDRIVE']) && !empty($_SERVER['HOMEPATH'])) {
      $home = $_SERVER['HOMEDRIVE'] . $_SERVER['HOMEPATH'];
    }

    if ($home === null) {
      throw new UnexpectedValueException('Could not locate home directory.');
    }

    return rtrim($home, '\\/');
  }

  private function getRefreshToken(Google_Client $client) {
    $client->setRedirectUri('urn:ietf:wg:oauth:2.0:oob');
    $client->setScopes('https://www.googleapis.com/auth/content');
    $client->setAccessType('offline'); // So that we get a refresh token

    printf("Visit the following URL and log in:\n\n\t%s\n\n",
        $client->createAuthUrl());
    print ('Then type the resulting code here: ');
    $code = trim(fgets(STDIN));
    $client->authenticate($code);

    $token = $client->getAccessToken();
    return $token['refresh_token'];
  }

  protected function cacheRefreshToken(Google_Client $client) {
    print (str_repeat('*', 40));
    print ("Your refresh token was missing or invalid, fetching a new one\n");
    $refreshToken = $this->getRefreshToken($client);
    $this->config->refreshToken = $refreshToken;
    file_put_contents($this->configFile,
        json_encode($this->config, JSON_PRETTY_PRINT));
    print ("Refresh token saved to your config file\n");
    print (str_repeat('*', 40));
  }

  protected function authenticate(Google_Client $client) {
    $accountFile = $this->configDir . '/' . self::SERVICE_ACCOUNT_FILE_NAME;
    $oauthFile = $this->configDir . '/' . self::OAUTH_CLIENT_FILE_NAME;

    if (file_exists($accountFile)) {
      $accountConfig = json_decode(file_get_contents($accountFile), true);
      $client->setAuthConfig($accountConfig);
    } else if (file_exists($oauthFile)) {
      $oauthConfig = json_decode(file_get_contents($oauthFile), true);
      $client->setAuthConfig($oauthConfig);
      if($this->config->refreshToken == null) {
        $this->cacheRefreshToken($client);
      } else {
        try {
          $client->refreshToken($this->config->refreshToken);
        } catch (Google_Auth_Exception $exception) {
          $this->cacheRefreshToken($client);
        }
      }
    } else {
      throw new Exception(sprintf('Could not find or read credentials at either'
          . '%s or %s.', $accountFile, $oauthFile));
    }
  }

  // Retry a function with back off
  protected function retry($function, $parameter, $maxAttempts = 5) {
    $attempts = 1;

    while ($attempts <= $maxAttempts) {
      try {
        return call_user_func(array($this, $function), $parameter);
      } catch (Google_Service_Exception $exception) {
        sleep($attempts * $attempts);
        $attempts++;
      }
    }
  }
}
