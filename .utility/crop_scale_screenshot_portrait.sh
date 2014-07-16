#!/bin/bash
#
# Script to remove top and bottom bar from an Android screenshot in portrait orientation.
#
# usage : ./crop_scale_screenshot_portraiti.sh image.png
#
# Dependency : ImageMagick
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

imageFile=$1

# get image properties and save as array (identify returns spaces seperated result)
imageProperties=(`identify $imageFile`)

# image size is 3rd element of the array
imageSize=${imageProperties[2]}

case "$imageSize" in
"1080x1920")	extract="1080x1700+0+75"
		resize="400x631"
		;;
"1920x1080")	extract="1792x1005+0+75"
		resize="713x400"
		;;
*)		echo $imageSize
		echo "Unknown format, image was not changed."
		exit 0
		;;
esac

echo "original size : $imageSize"
echo "extraction parameters : $extract"
echo "resize parameters : $resize"

# remove bars and resize image
convert -size $imageSize -extract $extract $imageFile -resize $resize $imageFile
