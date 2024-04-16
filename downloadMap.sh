#!/bin/sh
#This script downloads the OSM data by hitting the Overpass API

#Set default bounding box
minLon=-84.7872
maxLon=-84.6941
minLat=39.4929
maxLat=39.5209

echo "Downloading OSM data for bounding box: $minLon,$minLat,$maxLon,$maxLat"

wget -O ./mapData.json "https://overpass-api.de/api/interpreter?data=[out:json];(node($minLat,$minLon,$maxLat,$maxLon);<;);out%20meta;" && echo "Last Success Downloaded at $(date)" > "./map-download-history.txt"