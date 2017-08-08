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
"""Creates a sample accounttax object for the accounttax samples."""


def create_accounttax_sample(account_id, **overwrites):
  """Creates a sample accounttax resource object for the accounttax samples.

  Args:
      account_id: int, Merchant Center ID these tax settings are for.
      **overwrites: dictionary, a set of accounttax attributes to overwrite

  Returns:
      A new accounttax resource in dictionary form.
  """
  tax = {
      'accountId': account_id,
      'rules': [{
          'country': 'US',
          'locationId': 21167,
          'useGlobalRate': True
      }]
  }
  tax.update(overwrites)
  return tax
