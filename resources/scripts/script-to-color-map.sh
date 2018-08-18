#!/bin/bash

SCRIPT=./resources/scripts/show-biome-swatches.sh
#SCRIPT=./resources/scripts/show-altitude-swatches.sh
cat $SCRIPT | \
  awk -F\; '{print ":XXX {:red " $3 " :green " $4 " :blue " $5}' | \
  awk -Fm '{print $1 "}"}'
