#!/bin/bash
#
# Script to deploy Android SDK jar files to local maven repository.
#
# Copyright (C) 2014 Dieter Adriaenssens
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

# deploy android SDK jar to local maven repository
if [[ -d maven-android-sdk-deployer/.git ]]; then cd maven-android-sdk-deployer; git pull; else git clone https://github.com/mosabua/maven-android-sdk-deployer.git; cd maven-android-sdk-deployer; fi
# only deploy SDK 4.4W (API 20)
mvn clean install -pl platforms/android-20

# return to initial path
cd ..
