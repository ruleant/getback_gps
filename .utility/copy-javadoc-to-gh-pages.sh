#!/bin/bash
#
# Script to copy generated JavaDoc to a folder on Github pages (gh-pages).
# It is called by Travis CI, after a succesful build.
#
# This script was originally written by maxiaohao in the aws-mock GitHub project.
# origin : https://github.com/treelogic-swe/aws-mock/blob/04746419b409e1689632da53a7ea6063dbe33ef8/.utility/push-javadoc-to-gh-pages.sh
#
# Copyright 2013 Xiaohao Ma
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# don't update JavaDoc when it is a pull request
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

echo -e "Start to publish lastest Javadoc to gh-pages...\n"

  cp -R build/docs/javadoc $HOME/javadoc-latest

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ruleant/getback_gps gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Done magic with auto publishment to gh-pages.\n"
  
fi
