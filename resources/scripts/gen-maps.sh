#!/bin/bash

MACHINE_RES_X=1600
MACHINE_RES_Y=1020
HUMAN_RES_X=3608
HUMAN_RES_Y=2300

mkdir resources/planets/gen

## World images to be consumed by code
docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C Biomes.col -c \
    -l 155 \
    > resources/planets/gen/sinusoidal-biomes.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C Altitude.col \
    -l 155 \
    > resources/planets/gen/sinusoidal-altitude.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C LandSea.col \
    -l 155 \
    > resources/planets/gen/sinusoidal-ls.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C LandSea.col -c \
    -l 155 \
    > resources/planets/gen/sinusoidal-lsi.bmp


## World images to be viewed by humans
docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C Biomes.col -c \
    > resources/planets/gen/mercator-biomes.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C Altitude.col \
    > resources/planets/gen/mercator-altitude.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C greyscale.col \
    > resources/planets/gen/mercator-bump.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C LandSea.col -c \
    > resources/planets/gen/mercator-lsi.bmp

## Starting zone images to be viewed by humans
docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pa -C Altitude.col \
    -l 330 -L 15 -m 2.3 \
    > resources/planets/gen/startzone-altitude.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pa -C Biomes.col -c \
    -l 330 -L 15 -m 2.3 \
    > resources/planets/gen/startzone-biomes.bmp
