GetBack GPS for Android
=======================

[![GetBack GPS](http://img.shields.io/badge/release-v0.4.1-blue.svg)](https://f-droid.org/repository/browse/?fdid=com.github.ruleant.getback_gps)
[![Build Status](https://travis-ci.org/ruleant/getback_gps.svg?branch=master)](https://travis-ci.org/ruleant/getback_gps)
[![Codacy Badge](https://api.codacy.com/project/badge/0040e146618e41ac9c39d04c7b1a3fef)](https://www.codacy.com/app/ruleant/getback_gps)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2277/badge.svg)](https://scan.coverity.com/projects/2277)
[![Coverage Status](https://coveralls.io/repos/ruleant/getback_gps/badge.png?branch=master)](https://coveralls.io/r/ruleant/getback_gps?branch=master)
[![Translation status](http://hosted.weblate.org/widgets/getback_gps-shields-badge.png)](http://hosted.weblate.org/engage/getback_gps/?utm_source=widget)
[![Stack Share](http://img.shields.io/badge/tech-stack-0690fa.svg?style=flat)](http://stackshare.io/ruleant/getback-gps)

[![Buildtime trend](https://buildtimetrend.herokuapp.com/badge/ruleanCrt/getback_gps/latest)](http://ruleant.github.io/getback_gps/buildtime-trend/)
[![Total builds](https://buildtimetrend.herokuapp.com/badge/ruleant/getback_gps/builds/month)](http://ruleant.github.io/getback_gps/buildtime-trend/)
[![Passed build jobs](https://buildtimetrend.herokuapp.com/badge/ruleant/getback_gps/passed/month)](http://ruleant.github.io/getback_gps/buildtime-trend/)

Open Source Android app for finding your way back to a previously visited location using GPS coordinates.

Imagine visiting a town, going to an event or doing some hiking. When finished, you have to find your car again, or the way to the station, or any other point where you started.
Store a location when you start your trip, and at the end of the day, use the app to find your way back to where you started.

More info on our [website](http://ruleant.github.io/getback_gps).

Features
--------

- determine location based on GPS signal, WiFi or cell phone network :
    - use best/most accurate location provider
    - get location updates automatically (based on a time interval or change of location) or
    - get location on demand (by manually refreshing)
- save a location and give it a name
- ‘get back’ mode : indicator pointing to a stored location (showing distance and direction)
- indicate current speed and current bearing
- use built-in sensors to get more accurate and quicker updated bearing
- compass rose indicating North
- detect travel direction and adjust relative direction to destination accordingly

How to get it?
--------------

The [latest version](https://f-droid.org/repository/browse/?fdid=com.github.ruleant.getback_gps) is available on [F-Droid](https://f-droid.org/). Install the apk directly from the F-Droid website, or use the [F-Droid app](https://f-droid.org/FDroid.apk) to keep automaticaly up to date with future releases.

If you want to try out the latest development version, you can get the code from the [git repository](https://f-droid.org/FDroid.apk) and [build the current development version](https://github.com/ruleant/getback_gps/wiki/Development) yourself.


Bugs and feature requests
-------------------------

Please report bugs and add feature requests in the Github [issue tracker](https://github.com/ruleant/getback_gps/issues).

Requests for translations to a new language can be made here as well.

Translation
-----------

You can help translating the app to your own language : <http://hosted.weblate.org/projects/getback_gps/strings>.

[![Translation status](http://hosted.weblate.org/widgets/getback_gps-287x66-grey.png)](http://hosted.weblate.org/engage/getback_gps/?utm_source=widget)

Is your language not listed? Follow the instructions on the [New translation](https://hosted.weblate.org/projects/getback_gps/strings/#new-lang) tab in Weblate or open an issue in the Github [issue tracker](https://github.com/ruleant/getback_gps/issues).

Code documentation
------------------

Code documentation of all classes and their methods can be found [here](http://ruleant.github.io/getback_gps/javadoc/index.html).

Third party material
--------------------

- res/drawable-*/ic_action_refresh.png
    - From the Action Bar Icon Pack, see http://developer.android.com/design/style/iconography.html
    - origin : http://developer.android.com/downloads/design/Android_Design_Icons_20131106.zip
    - license : Apache 2.0

- src/com/github/ruleant/getback_gps/lib/FormatUtils.java
    - Method FormatUtils.formatDist() is based on method formatDist in class MixUtils that is part of mixare
    - origin : http://www.java2s.com/Code/Android/Date-Type/FormatDistance.htm
    - license : GPLv3+

- .utility/copy-javadoc-to-gh-pages.sh
    - This script was originally written by Xiaohao Ma in the aws-mock project and modified by Ben Limmer.
      See script file for more details.
    - license : Apache 2.0

- .utility/remove-incomplete-translations
    - Based on scripts/remove-incomplete-mo of the phpMyAdmin project
    - origin : https://github.com/phpmyadmin/phpmyadmin/blob/master/scripts/remove-incomplete-mo
    - license : GPLv2

- Crouton library
    - version : v1.8.1
    - website : https://github.com/keyboardsurfer/Crouton
    - license : Apache 2.0

Credits
-------

Getback GPS for Android was made possible by many contributers and sponsors, see [Credits](https://github.com/ruleant/getback_gps/wiki/Credits).

License
-------

Copyright (C) 2012-2015 Dieter Adriaenssens <ruleant@users.sourceforge.net>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
