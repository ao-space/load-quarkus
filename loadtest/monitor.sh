#!/bin/bash

## About preventing grep result from showing 'grep' self, 
## pls see also: https://unix.stackexchange.com/a/74186

while true
do
  echo "-----------------------------------------------"
  ps -o pid,rss,%cpu,command | grep -e "[l]oad-*"
  sleep 2
done