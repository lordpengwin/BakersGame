#! /usr/bin/env bash
#
# Run Bakers Game
# usage: bakersgame.sh <boardfile>

mvn exec:java -Dexec.args=$1
exit 0

