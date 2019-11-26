<?php
/**
 * Copyright 2017 Google Inc. All Rights Reserved.
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

require_once 'AccountstatusesSample.php';
require_once 'AccounttaxSample.php';
require_once 'BaseSample.php';
require_once 'DatafeedsSample.php';
require_once 'MultiClientAccountsSample.php';
require_once 'PrimaryAccountsSample.php';
require_once 'ProductsSample.php';
require_once 'ProductstatusesSample.php';
require_once 'ShippingsettingsSample.php';

class NonOrdersSamples extends BaseSample {

  public function run() {
    $accounts = new PrimaryAccountsSample($this->session);
    $accounts->run();
    $statuses = new AccountstatusesSample($this->session);
    $statuses->run();
    if ($this->session->mcaStatus) {
      $mcaAccounts = new MultiClientAccountsSample($this->session);
      $mcaAccounts->run();
    } else {
      $products = new ProductsSample($this->session);
      $products->run();
      $datafeeds = new DatafeedsSample($this->session);
      $datafeeds->run();
      $statuses = new ProductstatusesSample($this->session);
      $statuses->run();
    }
    $tax = new AccounttaxSample($this->session);
    $tax->run();
    $settings = new ShippingsettingsSample($this->session);
    $settings->run();
  }
}

$samples = new NonOrdersSamples();
$samples->run();
