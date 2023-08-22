# CSC2002S 
## PCP Assignment 1 SMNAIM002

## Getting started:
There are 4 rules/targets established in the Makefile:
* ```make```: This compiles all the files and saves the .class files in the bin/clubSimulation folder.
* ```make run```: This compiles and runs the program. 
* ```make clean```: This removes the .class files from the bin/clubSimulation folder.
#### *When using any of these, make sure you are in the main project folder: SMNAIM002_CSC2002S_PCP2

## Input parameters:
* noClubgoers: total people to enter room
* gridX: No. of X grid cells
* gridY: No. of Y grid cells
* max: max people allowed in club
### Input parameters are given in the form:
```bash
"noClubgoers gridX gridY max" 
```
#### *Adding inputs parameters to the make run command is optional, as there are default values specified in the program 

## make run with arguments:
Running the program with specified arguments is used as follows:
```bash
$ make run ARGS="<noClubgoers> <gridX> <gridY> <max>"
```