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
"""Run all example workflows (except for the Orders service)."""

from __future__ import absolute_import
from __future__ import print_function

import sys

from shopping.content import accounts
from shopping.content import accountstatuses
from shopping.content import accounttax
from shopping.content import common
from shopping.content import datafeeds
from shopping.content import products
from shopping.content import productstatuses
from shopping.content import shippingsettings


def main(argv):
  # Authenticate and construct service.
  service, config, _ = common.init(argv, __doc__)

  print('--------------------------------')
  accounts.workflow(service, config)
  print('--------------------------------')
  accountstatuses.workflow(service, config)
  print('--------------------------------')
  accounttax.workflow(service, config)
  print('--------------------------------')
  datafeeds.workflow(service, config)
  print('--------------------------------')
  products.workflow(service, config)
  print('--------------------------------')
  productstatuses.workflow(service, config)
  print('--------------------------------')
  shippingsettings.workflow(service, config)
  print('--------------------------------')


if __name__ == '__main__':
  main(sys.argv)
