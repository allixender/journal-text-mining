#!/bin/bash

BASEURL="http://www.tandfonline.com"
MARINE="/toc/tnzm20"
let MA_MAX=49

GEOS="/toc/tnzg20"
let GE_MAX=58

let countMarine=o
let countGeol=0

CKEY="#.VaUEdnVDPxj"

while [ ! $countGeol -ge $GE_MAX ]; do

  let issue=0
  let countGeol=countGeol+1

  while [ ! $issue -ge 4 ]; do

    let issue=issue+1
    # echo "marine $countGeol"
    echo "${BASEURL}${GEOS}/${countGeol}/${issue}"
    wget -O geol-${countGeol}-${issue}.html "${BASEURL}${GEOS}/${countGeol}/${issue}"
  done

done
