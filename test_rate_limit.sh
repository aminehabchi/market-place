#!/bin/bash

# URL to test
URL="http://localhost:10000/api/products/"  # change to your route

# Number of requests to send
MAX_REQUESTS=20

# Delay between requests (in seconds, can be 0.1 for 100ms)
DELAY=0.1

for i in $(seq 1 $MAX_REQUESTS)
do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL)
    if [ "$STATUS" -eq 429 ]; then
        echo "[$i] Rate limit reached! Status: $STATUS"
    else
        echo "[$i] Request successful. Status: $STATUS"
    fi
    sleep $DELAY
done