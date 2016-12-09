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

"""Common utils for the Content API for Shopping samples."""
from __future__ import print_function

import argparse
import json
import os
import sys
import time

import _constants
import auth
from googleapiclient import discovery
import httplib2
from oauth2client import tools


# Authenticate and return the Content API service along with any command-line
# flags/arguments.
def init(argv, doc, parents=None):
  """A common initialization routine for the Content API samples.

  Args:
    argv: list of string, the command-line parameters of the application.
    doc: string, description of the application. Usually set to __doc__.
    parents: list of argparse.ArgumentParser, additional command-line flags.

  Returns:
    A tuple of (service, config, flags), where service is the service object,
    config is the configuration JSON in Python form, and flags
    are the parsed command-line flags.
  """
  service = None
  flags = None
  parent_parsers = [tools.argparser]
  if parents is not None:
    parent_parsers.extend(parents)

  parser = argparse.ArgumentParser(
      description=doc,
      formatter_class=argparse.RawDescriptionHelpFormatter,
      parents=parent_parsers)
  flags = parser.parse_args(argv[1:])

  if not os.path.isfile(_constants.CONFIG_FILE):
    print('No sample configuration file found. Checked:', file=sys.stderr)
    print(' - %s' % _constants.CONFIG_FILE, file=sys.stderr)
    print('Please read the accompanying documentation.', file=sys.stderr)
    sys.exit(1)

  config = json.load(open(_constants.CONFIG_FILE, 'r'))
  credentials = auth.authorize(config, flags)
  http = credentials.authorize(http=httplib2.Http())
  service = discovery.build(
      _constants.SERVICE_NAME, _constants.SERVICE_VERSION, http=http)
  return (service, config, flags)

unique_id_increment = 0


def get_unique_id():
  """Generates a unique ID.

  The ID is based on the current UNIX timestamp and a runtime increment.

  Returns:
    A unique string.
  """
  global unique_id_increment
  if unique_id_increment is None:
    unique_id_increment = 0
  unique_id_increment += 1
  return '%d%d' % (int(time.time()), unique_id_increment)


def check_mca(config, should_be_mca, msg = None):
  """Checks that the configured account is an MCA or not based on the argument.

  If it is not, it exits the program early."""
  is_mca = 'isMCA' in config and config['isMCA']

  if should_be_mca != is_mca:
    if msg is not None:
      print(msg)
    else:
      print('For this sample, you must%s use a multi-client account.' %
            (' not' if is_mca else ''))
    sys.exit(1)

def json_absent_or_false(json, key):
  """Checks if the key does not appear or maps to false in the JSON object.

  Our measure of false here is the same as Python.
  """
  return key not in json or not json[key]
