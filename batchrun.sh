#!/bin/sh


NUM_OF_JOB=50

for IN in `seq 1 $NUM_OF_JOB`; do

qsub -N INTROSPECT_$IN testrun

done
