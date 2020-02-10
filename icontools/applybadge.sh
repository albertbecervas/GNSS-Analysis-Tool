#!/bin/sh

INPUT_FILE="$1"
BADGE=$2
DIMENSION=$3
PLATFORM=$4
OUTPUT_FILE="$5"

PLAFORM_IOS="ios"
PLAFORM_ANDROID="android"

if [ "$PLATFORM" == "$PLAFORM_ANDROID" ]; then
	export PATH="/usr/local/bin:/usr/bin:/bin"	
fi

convertPath=`which convert`
if [[ ! -f ${convertPath} || -z ${convertPath} ]]; then
  echo "WARNING: Skipping Icon versioning, you need to install ImageMagick and ghostscript (fonts) first, you can use brew to simplify process:
  brew install imagemagick
  brew install ghostscript"
exit 0;
fi

if [ -z "${DIMENSION//[0-9]/}" ]; then #casting dimension as number
	if [[ "$PLATFORM" == "$PLAFORM_IOS" ]]; then
		badge_name="ios_$BADGE"".pdf"
	fi
	if [ "$PLATFORM" == "$PLAFORM_ANDROID" ]; then
		badge_name="android_$BADGE"".pdf"
	fi

	if [ -z "$badge_name" ]; then #awolled platform 
		echo "No correct platform [ios, android]"
	else
		if [ ! -d "tmp" ]; then
			mkdir "tmp"
		fi

		size=`convert "$INPUT_FILE" -format "%wx%h" info:`
		convert "$INPUT_FILE" \( badges/$badge_name -resize "$size" \) -composite -gravity center tmp/tmp_with_badge.png
		convert tmp/tmp_with_badge.png -resize $DIMENSIONx$DIMENSION "$OUTPUT_FILE"

		rm -r "tmp"
	fi
else
	echo "Dinemsion must be a valid number"
fi
