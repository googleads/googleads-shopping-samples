<?php
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
  protected $sandboxService;
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
      throw new InvalidArgumentException(sprintf('The config file at %s is not '
          . 'valid JSON format. You can use the config.json file in the '
          . 'samples root as a template.', $this->configFile));
    }

    $client = new Google_Client();
    $client->setApplicationName($this->config->applicationName);
    $client->setScopes(Google_Service_ShoppingContent::CONTENT);
    $this->authenticate($client);

    $this->merchantId = $this->config->merchantId;
    $this->websiteUrl = $this->config->websiteUrl;
    $this->service = new Google_Service_ShoppingContent($client);
    $this->sandboxService = $this->createSandbox($client);
  }

  /**
   * Instead of requiring the v2sandbox library, we'll instead just
   * make a version of the service that calls the sandbox API endpoint.
   * Changing $sandboxService->servicePath is not enough, since that
   * value is cached during construction of the objects that correspond
   * to the API services, so we use reflection to access the caching (private)
   * field of the $sandboxService->orders object and change it there instead.
   */
  private function createSandbox($client) {
    $class = new ReflectionClass("Google_Service_Resource");
    $property = $class->getProperty("servicePath");
    $property->setAccessible(true);

    $sandboxService = new Google_Service_ShoppingContent($client);
    $orders = $sandboxService->orders;
    $property->setValue($orders, 'content/v2sandbox/');

    return $sandboxService;
   }

  abstract public function run();

  /**
   * This function is used as a gate for methods that can only be run
   * on multi-client accounts.
   *
   * @throws InvalidArgumentException if the config does not contain an MCA.
   */
  const MCA_MSG = 'This operation can only be run on multi-client accounts.';
  public function mustBeMCA($msg = MCA_MSG) {
    if(!$this->config->isMCA) {
      throw new InvalidArgumentException($msg);
    }
  }

  /**
   * This function is used as a gate for methods that cannot be run
   * on multi-client accounts.
   *
   * @throws InvalidArgumentException if the config contains an MCA.
   */
  const NON_MCA_MSG = 'This operation cannot be run on multi-client accounts.';
  public function mustNotBeMCA($msg = NON_MCA_MSG) {
    if($this->config->isMCA) {
      throw new InvalidArgumentException($msg);
    }
  }

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

  private function getToken(Google_Client $client) {
    $client->setRedirectUri('urn:ietf:wg:oauth:2.0:oob');
    $client->setScopes('https://www.googleapis.com/auth/content');
    $client->setAccessType('offline'); // So that we get a refresh token

    printf("Visit the following URL and log in:\n\n\t%s\n\n",
        $client->createAuthUrl());
    print ('Then type the resulting code here: ');
    $code = trim(fgets(STDIN));
    $client->authenticate($code);

    return $client->getAccessToken();
  }

  protected function cacheToken(Google_Client $client) {
    print (str_repeat('*', 40));
    print ("Your token was missing or invalid, fetching a new one\n");
    $this->config->token = $this->getToken($client);
    file_put_contents($this->configFile,
        json_encode($this->config, JSON_PRETTY_PRINT));
    print ("Token saved to your config file\n");
    print (str_repeat('*', 40));
  }

  /**
   * This function looks for authentication in this order:
   * - Google Application Default Credentials
   * - Service account credentials in SERVICE_ACCOUNT_FILE_NAME in the configDir
   * - OAuth2 credentials in OAUTH_CLIENT_FILE_NAME in the configDir
   */
  protected function authenticate(Google_Client $client) {
    try {
      // Try loading the credentials.
      $credentials = Google\Auth\ApplicationDefaultCredentials::getCredentials(
          Google_Service_ShoppingContent::CONTENT);
      // If we got here, the credentials are there, so tell the client.
      $client->useApplicationDefaultCredentials();
      print "Using Google Application Default Credentials.\n";
    } catch (DomainException $exception) {
      // Safe to ignore this error, since we'll fall back on other creds.
      $this->authenticateFromConfig($client);
    }
  }

  // Handles loading authentication credentials from the config dir.
  protected function authenticateFromConfig(Google_Client $client) {
    $accountFile = $this->configDir . '/' . self::SERVICE_ACCOUNT_FILE_NAME;
    if (file_exists($accountFile)) {
      print "Loading service account credentials from" . $accountFile . ".\n";
      $accountConfig = json_decode(file_get_contents($accountFile), true);
      $client->setAuthConfig($accountConfig);
      $client->setScopes(Google_Service_ShoppingContent::CONTENT);
      return;
    }
    $oauthFile = $this->configDir . '/' . self::OAUTH_CLIENT_FILE_NAME;
    if (file_exists($oauthFile)) {
      print "Loading OAuth2 credentials from" . $oauthFile . ".\n";
      $oauthConfig = json_decode(file_get_contents($oauthFile), true);
      $client->setAuthConfig($oauthConfig);
      if($this->config->token == null) {
        $this->cacheToken($client);
      } else {
        try {
          $client->refreshToken($this->config->token->refresh_token);
        } catch (Google_Auth_Exception $exception) {
          $this->cacheToken($client);
        }
      }
      return;
    }
    // All authentication failed.
    $msg = sprintf('Could not find or read credentials from '
        . 'either the Google Application Default credentials, '
        . '%s, or %s.', $accountFile, $oauthFile);
    throw new DomainException($msg);
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
