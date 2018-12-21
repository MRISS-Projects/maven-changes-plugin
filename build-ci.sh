#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements. See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
# NOTE: For help with the syntax of this file, see:
# http://maven.apache.org/doxia/references/apt-format.html

set -e

cp maven/settings-security.xml $HOME/.m2

if [ "${TRAVIS_PULL_REQUEST}" = "true" ]; then
  echo This is a PR. Just testing ...
  mvn clean install
else
  if [ "${TRAVIS_BRANCH}" = "master" ]; then
    echo This is an official release at master. Deploying artifact and site ...
    mvn -s maven/settings.xml -P deployment clean deploy && mvn -P deployment site-deploy
  else
    if [ "${TRAVIS_BRANCH}" = "DEVELOP" ]; then
      echo This is a new on-goging development version. Just commit the coverage results ...
      mvn -s maven/settings.xml -P update-coverage clean install
    fi
  fi
fi