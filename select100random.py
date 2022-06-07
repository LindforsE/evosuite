import os
import random

# random seed
random.seed()

# enter correct directory
os.chdir("bin/SF100/")
# print(os.listdir())

# create variables
class_list = list()
rnd_hundred = list()
command_list = list()
dir_list = list()

""" # read SF100/classes.txt
with open("classes.txt", 'r') as file1:
    class_list = [lines.rstrip() for lines in file1]

# select 100 random
rnd_hundred = random.sample(class_list, 100)

with open("100chosenClasses.txt", 'w') as file3:
    for row in rnd_hundred:
        file3.write(row + '\n') """

with open("100chosenClasses.txt", 'r') as file1:
    rnd_hundred = [lines.rstrip() for lines in file1]

with open("../../outputVariables.txt", 'r') as varFile:
    outVars = varFile.readlines()[0]

# restructure
# "<directory>\t<class>" to
# "<directory>\t<file>\t<class>"
cmd_string = "$EVOSUITE -generateSuite -class {} {} -Dalgorithm=$1"
for row in rnd_hundred:
    dir_list.append(row.split('\t')[0])
    command_list.append(cmd_string.format(
        row.split('\t')[1], outVars))


# insert execution string
# "java -jar evosuite.jar -generateSuite -target <directory>/<jar-file> -class <class-name>"

# save to file
with open("../../execute100RndClasses.sh", 'w') as file2:
    file2.write("#!/bin/sh\ncd bin/SF100\n")
    for command_arg, dir_arg in zip(command_list, dir_list):
        file2.write('cd ' + dir_arg + '\n')
        file2.write(command_arg + '\n')
        file2.write('cd ..\n')
