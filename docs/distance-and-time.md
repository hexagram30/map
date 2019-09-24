Playing around with units and sizes:
* 1 stride = average of average men's and average women's stride = 0.7112 m
* The radius of a medieval city = 2000 strides (the circumference is ~ 12600 strides)
* 1 half-day journey = 12600, enough to walk to a town, make purchases, and return home in one day (full day, 25200 strides)
* 1 week's journey = 180,000 strides
* 1 month's journey = 720,000 strides
* walk the Earth's circumference = ~56,550,000 strides, or 18,000,000 * pi strides (this would take about  6.5 years by these calculations; not an unreasonable time for a trip that was in a straight line)

Conversions:
* 1 m = 1.406 strides
* 1 km = 1406 strides
* 1 km = 0.179986 leagues
* 1 league = 5.556 km
* 1 league = 7811.736 strides

Walking unladen:
* 3 mi/hr
* 5 km/hr
*  7030 strides/hr
* 0.9 leagues/hr

I like the idea of standardizing on leagues :-)
* 1 league/hr
* 7811.736 strides/hr
* 5.556 km/hr
* 3.452 mi/hr

In a day, a healthy unladen human can walk 20-30 miles in 8 hours. Let's just call that a _really_ healthy person can walk 10 leagues/day:
* 10 leagues/day
* 78,117.36 strides/day
* 55.56 km/day
* 34.52 mi/day

D&D thoughts on the matter:
* https://open5e.com/gameplay-mechanics/movement

I'd say that laden, that'd be equivalent to difficult terrain and thus distance/time halved:

* 10 leagues/day unladen
* 5 leagues/day laden or difficult terrain or generally slow

Now, back to world size and pixel size ... trying to get a sense of how much unknown territory can be explored for a given/standard map resolution.

## A city

If we say that instead of 2.6 km (referring to the code comment from this ticket's description) on a side, an average medieval "city" was `(/ 5.556 2) = 2.778` km on a side, then we have:
* an average medieval "city" being 0.5 leagues on a side
* area = 0.25 square leagues
* all sides = 2 leagues
* radius = 0.25 = 𝛑r<sup>2</sup> ⟹ √(0.25/𝛑) = r ⟹ 0.28 leagues
* diameter = 0.56 leagues
* circumference = 1.75929 leagues
* 2 hours to walk around it

## Walking for a day

* ~5 times around a city
* 10 leagues

## A planet

* 40,000 km circumference
* 7,200 leagues circumference
* 56,000,000 strides circumference

## Pixel

### As a day?

* 10 leagues on a side (per pixel)
* 78,000 strides on a side (per pixel)
* 720 pixels wide
* ~14 leagues from corner to corner
* 20 cities could fit on a side
* could hold 400 cities, packed tight

### As a half-day?

* 5 leagues on a side (per pixel)
* 39,000 strides on a side (per pixel)
* 1440 pixels wide
* ~7 leagues from corner to corner
* holds
* 10 cities could fit on a side
* could hold 100 cities, packed tight

### As two hours?

* 2 leagues on a side (per pixel)
* 15,600 strides on a side (per pixel)
* 3600 pixels wide
* ~2.8 leagues from corner to corner
* 4 cities could fit on a side
* could hold 16 cities, packed tight

### As an hour?

* 1 leagues on a side (per pixel)
* 7800 strides on a side (per pixel)
* 7200 pixels wide
* ~1.4 leagues from corner to corner
* 2 cities could fit on a side
* could hold 4 cities, packed tight

If we have 4 leagues as a good average distance between cities or major towns (this lets you walk there, spend a couple hours, and walk back), then:
* a city's radial distance to the boundary of another city's radial distance is actually going to 0.28 leagues + 2 leagues = 2.28
* a city + circular surround will have an area of 2.28<sup>2</sup> 𝛑= 16.3 square leagues
* a city + square surround will have an area of  (* 2.28 2)<sup>2</sup> = 20.8 square leagues

Then, for each of the following pixel setups above, we'd have an actual number of possible/average cities per pixel as:
* day pixel: 4.8 cities
* half-day pixel: 1.2 cities
* two hour pixel: one city (requires 4 other pixels -- 5 total -- for a complete city buffer area)
* one hour pixel: one city (nearly fills pixel; requires 19 other pixels -- 20 total -- for city and buffer area)

Looking again at the original 1600 pixel width ...

That's:
* 35,000 strides per pixel
* 4.5 leagues per pixel
* 20.25 sq leagues per pixel
*  1 buffered city per pixel
* ~10hrs 45mins to walk straight across
* ~15hrs from corner to corner
