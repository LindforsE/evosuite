import os
# import numpy
from matplotlib import pyplot
import pandas

# go to correct directory
os.chdir("bin/SF100")

# read directories for 100 chosen classes
dirFolders = set()
with open("100chosenClasses.txt", 'r') as file1:
    for item in file1:
        dirFolders.add(item.split('\t')[0])

# loop through each directory
for item in dirFolders:
    os.chdir(f"{item}/")
    # print(os.listdir())
    try:
        stat = pandas.read_csv("evosuite-report/statistics.csv", delimiter=',')
        # cat1 = stat['TARGET_CLASS']
        # print(stat.to_string())
    except FileNotFoundError:
        pass
    os.chdir("../")

# os.chdir("104_vuze/evosuite-report/")
# stat2 = pandas.read_csv("statistics.csv")
# stat2.plot()
# pyplot.show()
