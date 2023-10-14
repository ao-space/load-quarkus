#!/bin/bash

## usage:
## cat inflation_raw.csv | ./conv.sh > inflation.csv
c=0

while IFS= read -r line
do
  if [ $c == 0 ]; then
    echo "Id, $line"
  else 
    echo "$c,$line"
  fi
  c=$[$c+1]
done
