#!/bin/bash
#
# Script to copy generated JavaDoc to a folder on Github pages (gh-pages).
# It is called by Travis CI, after a succesful build.
#
# This script was originally written by maxiaohao in the aws-mock GitHub project.
# origin : https://github.com/treelogic-swe/aws-mock/blob/04746419b409e1689632da53a7ea6063dbe33ef8/.utility/push-javadoc-to-gh-pages.sh
# Updated by Ben Limmer : only update JavaDoc when master branch is built.
# see : https://github.com/ReadyTalk/swt-bling/commit/c1ab076ea07b97c19c43e509fe387fc4d1fdcab5
# Updated by Dieter Adriaenssens : see git history for changes.
#
# Copyright 2013 Xiaohao Ma
# Copyright 2013 Ben Limmer
# Copyright 2014 Dieter Adriaenssens
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
# only update Javadoc when master branch is built
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

echo -e "Start to publish lastest Javadoc to gh-pages...\n"

  cp -R build/docs/javadoc $HOME/javadoc-latest

  cd $HOME
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/${TRAVIS_REPO_SLUG} gh-pages > /dev/null
  cd gh-pages

  # set git user in gh-pages repo
  git config user.email "travis@travis-ci.org"
  git config user.name "travis-ci"

  git rm -rf ./javadoc
  cp -Rf $HOME/javadoc-latest ./javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Done magic with auto publishment to gh-pages.\n"
  
fi
