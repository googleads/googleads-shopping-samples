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
"""Creates a sample shippingsettings object for the shippingsettings samples."""


def create_shippingsettings_sample(**overwrites):
  """Creates a sample shippingsettings object for the shippingsettings samples.

  Args:
      **overwrites: dictionary, a set of resource attributes to overwrite

  Returns:
      A new shippingsettings resource in dictionary form.
  """
  shipping = {
      'postalCodeGroups': [],
      'services': [{
          'name':
              'USPS',
          'currency':
              'USD',
          'deliveryCountry':
              'US',
          'deliveryTime': {
              'minTransitTimeInDays': 3,
              'maxTransitTimeInDays': 7
          },
          'active':
              True,
          'rateGroups': [{
              'applicableShippingLabels': [],
              'singleValue': {
                  'flatRate': {
                      'value': '5.00',
                      'currency': 'USD'
                  }
              }
          }]
      }]
  }
  shipping.update(overwrites)
  return shipping
