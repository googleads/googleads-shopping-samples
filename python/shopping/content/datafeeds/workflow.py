#!/usr/bin/python
#
# Copyright 2017 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Example workflow using all the methods in the Datafeeds service."""

from __future__ import absolute_import
from __future__ import print_function

import json
import sys

from shopping.content import common
from shopping.content.datafeeds import sample


def print_datafeed(feed):
  """Prints a representation of the given datafeed."""
  print(json.dumps(feed, sort_keys=True, indent=2, separators=(',', ': ')))


def non_mca_workflow(service, config, page_size=50):
  """Performs the methods that can be used on non-MCA accounts.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
    page_size: The page size to use for calls to list methods.
  """
  # Just used to shorten later calls to the Datafeeds service
  df = service.datafeeds()

  merchant_id = config['merchantId']

  count = 0
  print('Printing settings of all datafeeds:')
  request = df.list(merchantId=merchant_id, maxResults=page_size)
  while request is not None:
    result = request.execute()
    feeds = result.get('resources')
    if not feeds:
      print('No feeds found.')
      break
    count += len(feeds)
    for feed in feeds:
      print_datafeed(feed)
    request = df.list_next(request, result)
  print('Status for %d accounts printed.\n' % count)

  name = 'feed%s' % common.get_unique_id()
  datafeed = sample.create_datafeed_sample(config, name)

  # Add datafeed.
  print('Inserting feed "%s":' % name, end='')
  new_feed = df.insert(merchantId=merchant_id, body=datafeed).execute()
  print('done.\n')
  feed_id = int(new_feed['id'])

  print('Retrieving (with retries) new datafeed (ID %d).' % feed_id)
  req = df.get(merchantId=merchant_id, datafeedId=feed_id)
  feed = common.retry_request(req)
  print('Feed settings:')
  print_datafeed(feed)
  print()

  # Delete datafeed.
  print('Deleting feed "%s"...' % name, end='')
  df.delete(merchantId=merchant_id, datafeedId=feed_id).execute()
  print('done.\n')


def workflow(service, config):
  """Calls all possible Datafeeds methods on the configured account.

  Args:
    service: The service object used to access the Content API.
    config: The samples configuration as a Python dictionary.
  """

  print('Performing the Datafeeds workflow.')
  print()

  if common.is_mca(config):
    print('Nothing to do, as MCAs contain no datafeeds.\n')
  else:
    non_mca_workflow(service, config)

  print('Done with Datafeeds workflow.')


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  workflow(service, config)


if __name__ == '__main__':
  main(sys.argv)
