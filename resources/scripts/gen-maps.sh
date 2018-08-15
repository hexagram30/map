#!/bin/bash

MACHINE_RES_X=1600
MACHINE_RES_Y=1020
HUMAN_RES_X=3608
HUMAN_RES_Y=2300


## World images to be consumed by code
docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C Biomes.col -c \
    -l 155 \
    > resources/planets/001-sinusoidal-biomes.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C Altitude.col \
    -l 155 \
    > resources/planets/001-sinusoidal-altitude.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C LandSea.col \
    -l 155 \
    > resources/planets/001-sinusoidal-ls.bmp

docker run hexagram30/planet -w $MACHINE_RES_X -h $MACHINE_RES_Y -pS -C LandSea.col -c \
    -l 155 \
    > resources/planets/001-sinusoidal-lsi.bmp


## World mages to be viewed by humans
docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C Biomes.col -c \
    > resources/planets/001-mercator-biomes.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pm -C Altitude.col \
    > resources/planets/001-mercator-altitude.bmp


## Starting zone mages to be viewed by humans
docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pa -C Altitude.col \
    -l 330 -L 15 -m 2.3 \
    > resources/planets/001-startzone-altitude.bmp

docker run hexagram30/planet -w $HUMAN_RES_X -h $HUMAN_RES_Y -pa -C Biomes.col -c \
    -l 330 -L 15 -m 2.3 \
    > resources/planets/001-startzone-biomes.bmp
