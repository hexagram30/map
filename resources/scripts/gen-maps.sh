#!/bin/bash

## World images to be consumed by code
docker run hexagram30/planet -w 1600 -h 1020 -pS -C Biomes.col -c \
    > resources/planets/001-sinusoidal-biomes.bmp

docker run hexagram30/planet -w 1600 -h 1020 -pS -C Altitude.col \
    > resources/planets/001-sinusoidal-altitude.bmp

docker run hexagram30/planet -w 1600 -h 1020 -pS -C LandSea.col \
    > resources/planets/001-sinusoidal-ls.bmp

docker run hexagram30/planet -w 1600 -h 1020 -pS -C LandSea.col -c \
    > resources/planets/001-sinusoidal-lsi.bmp

## World mages to be viewed by humans
docker run hexagram30/planet -w 3608 -h 2300 -pm -C Biomes.col -c \
    > resources/planets/001-mercator-biomes.bmp

docker run hexagram30/planet -w 3608 -h 2300 -pm -C Altitude.col \
    > resources/planets/001-mercator-altitude.bmp


## Starting zone mages to be viewed by humans
docker run hexagram30/planet -w 3608 -h 2300 -pa -C Altitude.col -l 330 -L 15 -m 2.3 \
    > resources/planets/001-startzone-altitude.bmp

docker run hexagram30/planet -w 3608 -h 2300 -pa -C Biomes.col -c -l 330 -L 15 -m 2.3 \
    > resources/planets/001-startzone-biomes.bmp
