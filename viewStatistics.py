import os
# import numpy
# from matplotlib import pyplot
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

os.chdir("104_vuze/evosuite-report/")
stat2 = pandas.read_csv("statistics.csv", delimiter=',')

# timelines to be merged (_T1 -> _T60)
#   CoverageTimeline
#   LineCoverageTimeline
#   BranchCoverageTimeline
#   ExceptionCoverageTimeline
#   WeakMutationCoverageTimeline
#   OutputCoverageTimeline
#   MethodCoverageTimeline
#   MethodNoExceptionCoverageTimeline
#   CBranchCoverageTimeline
#   FitnessTimeline
#   DiversityTimeline

coverageTimeline_list = [
    f"CoverageTimeline_T{num}" for num in range(1, 60)]
lineCoverageTimeline_list = [
    f"LineCoverageTimeline_T{num}" for num in range(1, 60)]
branchCoverageTimeline_list = [
    f"BranchCoverageTimeline_T{num}" for num in range(1, 60)]
exceptionCoverageTimeline_list = [
    f"ExceptionCoverageTimeline_T{num}" for num in range(1, 60)]
weakMutationCoverageTimeline_list = [
    f"WeakMutationCoverageTimeline_T{num}" for num in range(1, 60)]
outputCoverageTimeline_list = [
    f"OutputCoverageTimeline_T{num}" for num in range(1, 60)]
methodCoverageTimeline_list = [
    f"MethodCoverageTimeline_T{num}" for num in range(1, 60)]
methodNoExceptionCoverageTimeline_list = [
    f"MethodNoExceptionCoverageTimeline_T{num}" for num in range(1, 60)]
cbranchCoverageTimeline_list = [
    f"CBranchCoverageTimeline_T{num}" for num in range(1, 60)]
fitnessTimeline_list = [
    f"FitnessTimeline_T{num}" for num in range(1, 60)]
diversityTimeline_list = [
    f"DiversityTimeline_T{num}" for num in range(1, 60)]


coverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=coverageTimeline_list,
    name="overallCoverage")
lineCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=lineCoverageTimeline_list,
    name="lineCoverate")
branchCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=branchCoverageTimeline_list,
    name="branchCoverage")
exceptionCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=exceptionCoverageTimeline_list,
    name="exceptionCoverage")
weakMutationCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=weakMutationCoverageTimeline_list,
    name="weakMutationCoverage")
outputCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=outputCoverageTimeline_list,
    name="outputCoverage")
methodCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=methodCoverageTimeline_list,
    name="methodCoverage")
methodNoExceptionCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=methodNoExceptionCoverageTimeline_list,
    name="methodNoExceptionCoverage")
cbranchCoverageSeries = pandas.Series(
    data=stat2.squeeze(),
    index=cbranchCoverageTimeline_list,
    name="cbranchCoverage")
fitnessSeries = pandas.Series(
    data=stat2.squeeze(),
    index=fitnessTimeline_list,
    name="fitness")
diversitySeries = pandas.Series(
    data=stat2.squeeze(),
    index=diversityTimeline_list,
    name="diversity")

# pickle all series'
# pandas.to_pickle()

# fitnessSeries.plot()
# diversitySeries.plot()
# pyplot.show()
