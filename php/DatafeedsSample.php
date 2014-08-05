<?php

require_once 'BaseSample.php';

class DatafeedsSample extends BaseSample {
  // This constant defines how many example datafeeds to create in a batch
  const BATCH_SIZE = 10;

  public function run() {
    $example_datafeed =
        $this->insertDatafeed($this->createExampleDatafeed('feed123'));
    $example_datafeed_id = $example_datafeed->getId();
    $this->getDatafeed($example_datafeed_id);
    $this->updateDatafeed($example_datafeed);

    $example_datafeed_batch_names = array();

    for ($i = 0; $i < self::BATCH_SIZE; $i++) {
      $example_datafeed_batch_names[] = 'feed' . $i;
    }

    $example_datafeed_batch =
        $this->createExampleDatafeeds($example_datafeed_batch_names);
    $example_datafeed_batch_ids =
        $this->insertDatafeedBatch($example_datafeed_batch);
    $this->listDatafeeds();

    // There is a short period after creating a datafeed during which it may not
    // be deleted. In general use it would be unusual to do so anyway, but for
    // the purposes of this example we retry with back off.
    $this->retry("deleteDatafeed", $example_datafeed_id);
    $this->deleteDatafeedBatch($example_datafeed_batch_ids);
  }

  public function insertDatafeed(
      Google_Service_ShoppingContent_Datafeed $datafeed) {
    $response =
        $this->service->datafeeds->insert($this->merchant_id, $datafeed);
    printf("Datafeed created with ID %d\n", $response->getId());
    return $response;
  }

  public function getDatafeed($datafeed_id) {
    $datafeed =
        $this->service->datafeeds->get($this->merchant_id, $datafeed_id);
    printf("Retrieved datafeed %s: '%s'\n", $datafeed->getId(),
        $datafeed->getName());
  }

  public function updateDatafeed(
      Google_Service_ShoppingContent_Datafeed $datafeed) {
    // Changing the scheduled fetch time
    $original = $datafeed->getFetchSchedule()->getHour();
    $datafeed->getFetchSchedule()->setHour(7);

    $response = $this->service->datafeeds->update($this->merchant_id,
        $datafeed->getId(), $datafeed);

    printf("Scheduled fetch time changed from %d:00 to %d:00\n", $original,
        $response->getFetchSchedule()->getHour());
  }

  public function deleteDatafeed($datafeed_id) {
    // The response for a successful delete is empty
    $this->service->datafeeds->delete($this->merchant_id, $datafeed_id);
    print ("Deleted test data feed\n");
  }

  public function insertDatafeedBatch($datafeeds) {
    $entries = array();

    foreach ($datafeeds as $key => $datafeed) {
      $entry =
          new Google_Service_ShoppingContent_DatafeedsCustomBatchRequestEntry();
      $entry->setMethod('insert');
      $entry->setBatchId($key);
      $entry->setDatafeed($datafeed);
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_DatafeedsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_response = $this->service->datafeeds->custombatch($batch_request);

    printf("Inserted %d datafeeds.\n", count($batch_response->entries));

    $ids = array();

    foreach ($batch_response->entries as $entry) {
      if (!empty($entry->getErrors())) {
        printf("There was an error inserting a datafeed: %s\n",
            $entry->getErrors()[0]->getMessage());
      } else {
        $datafeed = $entry->getDatafeed();
        $ids[] = $datafeed->getId();
        printf("Inserted datafeed %d for file '%s'\n", $datafeed->getId(),
            $datafeed->getFileName());
      }
    }

    return $ids;
  }

  public function listDatafeeds() {
    // There is a low limit on the number of datafeeds per account, so the list
    // method always returns all datafeeds.
    $datafeeds = $this->service->datafeeds->listDatafeeds($this->merchant_id);

    foreach ($datafeeds->getResources() as $datafeed) {
      printf("%s %s\n", $datafeed->getId(), $datafeed->getName());
    }
  }

  public function deleteDatafeedBatch($ids) {
    $entries = array();

    foreach ($ids as $key => $id) {
      $entry =
          new Google_Service_ShoppingContent_DatafeedsCustomBatchRequestEntry();
      $entry->setMethod('delete');
      $entry->setBatchId($key);
      $entry->setDatafeedId($id);
      $entry->setMerchantId($this->merchant_id);

      $entries[] = $entry;
    }

    $batch_request =
        new Google_Service_ShoppingContent_DatafeedsCustomBatchRequest();
    $batch_request->setEntries($entries);

    $batch_responses = $this->service->datafeeds->custombatch($batch_request);

    $errors = 0;
    foreach ($batch_responses->entries as $entry) {
      if (!empty($entry->getErrors())) {
        $errors++;
      }
    }
    print "Requested delete of batch inserted test datafeeds\n";
    printf("There were %d errors\n", $errors);
  }

  private function createExampleDatafeeds($names) {
    $datafeeds = array();

    foreach ($names as $name) {
      $datafeeds[] = $this->createExampleDatafeed($name);
    }

    return $datafeeds;
  }

  private function createExampleDatafeed($name) {
    $datafeed = new Google_Service_ShoppingContent_Datafeed();

    // The file name must unique per account, so we add a unique part to avoid
    // clashing with any existing feeds.
    $filename = $name . uniqid();

    $datafeed->setName($name);
    $datafeed->setContentType('products');
    $datafeed->setAttributeLanguage('en');
    $datafeed->setContentLanguage('en');
    $datafeed->setIntendedDestinations(array('Shopping'));
    $datafeed->setFileName($filename . '.txt');
    $datafeed->setTargetCountry('US');

    $fetch_schedule =
        new Google_Service_ShoppingContent_DatafeedFetchSchedule();
    /* You can schedule monthly, weekly or daily.

    Monthly - set day of month and hour
      $fetch_schedule->setDayOfMonth(...);
      $fetch_schedule->setHour(...);

    Weekly - set day of week and hour
      $fetch_schedule->setWeekday(...);
      $fetch_schedule->setHour(...);

    Daily - set just the hour
      $fetch_schedule->setHour(..);
    */
    $fetch_schedule->setWeekday('monday');
    $fetch_schedule->setHour(6);
    $fetch_schedule->setTimeZone('America/Los_Angeles');
    $fetch_schedule->setFetchUrl('https://feeds.myshop.com/' . $name);
    $fetch_schedule->setUsername('feedsuser');
    $fetch_schedule->setPassword('F33d5u4eR');

    $format = new Google_Service_ShoppingContent_DatafeedFormat();
    $format->setFileEncoding('utf-8');
    $format->setColumnDelimiter('tab');
    $format->setQuotingMode('value quoting');

    $datafeed->setFetchSchedule($fetch_schedule);
    $datafeed->setFormat($format);

    return $datafeed;
  }
}

$sample = new DatafeedsSample();
$sample->run();
