GetBack GPS for Android
=======================

[![GetBack GPS](https://img.shields.io/badge/release-v0.8-blue.svg)](https://f-droid.org/repository/browse/?fdid=com.github.ruleant.getback_gps)
[![Build Status](https://app.travis-ci.com/ruleant/getback_gps.svg?branch=master)](https://app.travis-ci.com/ruleant/getback_gps)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4b5357e56e194b2fade4282f8dc7c182)](https://app.codacy.com/gh/ruleant/getback_gps)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2277/badge.svg)](https://scan.coverity.com/projects/2277)
[![Coverage Status](https://coveralls.io/repos/github/ruleant/getback_gps/badge.svg?branch=master)](https://coveralls.io/github/ruleant/getback_gps?branch=master)

[![Translation status](https://hosted.weblate.org/widgets/getback_gps/-/svg-badge.svg)](https://hosted.weblate.org/engage/getback_gps/)
[![todofy badge](https://todofy.org/b/ruleant/getback_gps)](https://todofy.org/r/ruleant/getback_gps)
[![Stack Share](https://img.shields.io/badge/tech-stack-0690fa.svg?style=flat)](https://stackshare.io/ruleant/getback-gps)

Open Source Android app for finding your way back to a previously visited location using GPS coordinates.

Imagine visiting a town, going to an event or doing some hiking. When finished, you have to find your car again, or the way to the station, or any other point where you started.
Store a location when you start your trip, and at the end of the day, use the app to find your way back to where you started.

More info on the [website](https://ruleant.github.io/getback_gps).

Features
--------

- determine location based on GPS signal, WiFi or cell phone network :
    - use best/most accurate location provider
    - get location updates automatically (based on a time interval or change of location) or
    - get location on demand (by manually refreshing)
- save current location as destination
- add a destination with manual coordinates
- ‘get back’ mode : indicator pointing to a stored location (showing distance and direction)
- indicate current speed and current bearing
- use built-in sensors to get more accurate and quicker updated bearing
- a compass rose that points to the North
- detect travel direction and adjust relative direction to destination accordingly

How to get it?
--------------

The [latest version](https://f-droid.org/repository/browse/?fdid=com.github.ruleant.getback_gps) is available on [F-Droid](https://f-droid.org/). Install the apk directly from the F-Droid website, or use the [F-Droid app](https://f-droid.org/FDroid.apk) to keep automaticaly up to date with future releases.

If you want to try out the latest development version, you can get the code from the [git repository](https://github.com/ruleant/getback_gps) and [build the current development version](https://github.com/ruleant/getback_gps/wiki/Development) yourself.


Bugs and feature requests
-------------------------

Please report bugs and add feature requests in the Github [issue tracker](https://github.com/ruleant/getback_gps/issues).

Requests for translations to a new language can be made here as well.

Translation
-----------

You can help translating the app to your own language : <https://hosted.weblate.org/engage/getback_gps/>.

[![Translation status](https://hosted.weblate.org/widgets/getback_gps/-/multi-auto.svg)](https://hosted.weblate.org/engage/getback_gps/)

Is your language not listed? Follow the instructions on the [New translation](https://hosted.weblate.org/new-lang/getback_gps/strings/) tab in Weblate.

Code documentation
------------------

Code documentation of all classes and their methods can be found [here](https://ruleant.github.io/getback_gps/javadoc/index.html).

Third party material
--------------------

- res/drawable-*/ic_action_refresh.png
    - From the Action Bar Icon Pack, see http://developer.android.com/design/style/iconography.html
    - origin : https://developer.android.com/downloads/design/Android_Design_Icons_20131106.zip
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

Copyright (C) 2012-2021 Dieter Adriaenssens <ruleant@users.sourceforge.net>
Copyright (C) 2019 Timotheus Constambeys
Copyright (C) 2022 Jan Scheible

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
