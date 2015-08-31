#!/bin/bash
#ocrpdftotext
# Simplified implementation of http://ubuntuforums.org/showthread.php?t=880471

# Might consider doing something with getopts here, see http://wiki.bash-hackers.org/howto/getopts_tutorial
DPI=300
TESS_LANG=eng

FILENAME=${@}
TMP_NAME=`basename "$FILENAME" .pdf`
OUTPUT_FILENAME=${TMP_NAME}-out-${DPI}.txt

PAGES=`pdfinfo "$FILENAME" | grep Pages | sed -r  "s/^[^0-9]*([0-9]+)$/\1/"`

for i in `seq 1 $PAGES`; do
  convert -density ${DPI} -depth 8 -background white -flatten +matte ${FILENAME}\[$(($i - 1 ))\] "${TMP_NAME}-${i}.tif"
done

parallel "tesseract {} {.} " ::: ${TMP_NAME}-*.tif

for i in `seq 1 $PAGES`; do cat ${TMP_NAME}-${i}.txt; done >> "${OUTPUT_FILENAME}"

