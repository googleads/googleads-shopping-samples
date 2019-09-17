#!/usr/bin/python
#
# Copyright 2016 Google Inc. All Rights Reserved.
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
"""Updates the specified datafeed on the specified account."""

from __future__ import print_function
import argparse
import sys

from shopping.content import common

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('datafeed_id', help='The ID of the datafeed to update.')


def main(argv):
  # Authenticate and construct service.
  service, config, flags = common.init(
      argv, __doc__, parents=[argparser])
  merchant_id = config['merchantId']
  datafeed_id = flags.datafeed_id

  # Get the datafeed to be changed
  datafeed = service.datafeeds().get(
      merchantId=merchant_id, datafeedId=datafeed_id).execute()

  # Changing the scheduled fetch time to 7:00.
  datafeed['fetchSchedule']['hour'] = 7
  request = service.datafeeds().update(
      merchantId=merchant_id, datafeedId=datafeed_id, body=datafeed)

  result = request.execute()
  print('Datafeed with ID %s and fetchSchedule %s was updated.' %
        (result['id'], str(result['fetchSchedule'])))


if __name__ == '__main__':
  main(sys.argv)
