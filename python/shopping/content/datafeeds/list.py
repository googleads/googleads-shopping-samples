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
"""Gets all datafeeds on the specified account."""

from __future__ import print_function
import sys

from shopping.content import common


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)
  merchant_id = config['merchantId']

  request = service.datafeeds().list(merchantId=merchant_id)

  while request is not None:
    result = request.execute()
    datafeeds = result.get('resources')
    if not datafeeds:
      print('No datafeeds were found.')
      break
    for datafeed in datafeeds:
      print('Datafeed %s with name "%s" was found.' %
            (datafeed['id'], datafeed['name']))
    request = service.datafeeds().list_next(request, result)


if __name__ == '__main__':
  main(sys.argv)
