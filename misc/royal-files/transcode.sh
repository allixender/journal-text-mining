#!/bin/bash
#ocrpdftotext
# Simplified implementation of http://ubuntuforums.org/showthread.php?t=880471

# Might consider doing something with getopts here, see http://wiki.bash-hackers.org/howto/getopts_tutorial
DPI=300
TESS_LANG=eng

FILENAME=${@}
SCRIPT_NAME=`basename "$0" .sh`
TMP_NAME=`basename "$FILENAME" .pdf`
TMP_DIR=${TMP_NAME}-tmp
OUTPUT_FILENAME=${TMP_NAME}-out-${DPI}.txt

mkdir ${TMP_DIR}
cp ${@} ${TMP_DIR}
cd ${TMP_DIR}

PAGES=`pdfinfo "$FILENAME" | grep Pages | sed -r  "s/^[^0-9]*([0-9]+)$/\1/"`

for i in `seq 1 $PAGES`; do

  convert -density ${DPI} -depth 8 -background white -flatten +matte ${FILENAME}\[$(($i - 1 ))\] "${TMP_NAME}-${i}.tif"
  
  tesseract "${TMP_NAME}-${i}.tif" - -l ${TESS_LANG} >> "${OUTPUT_FILENAME}"

done

mv ${OUTPUT_FILENAME} ..
rm *
cd ..
rmdir ${TMP_DIR}

