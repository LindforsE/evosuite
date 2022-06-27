import os
# import numpy
from matplotlib import pyplot
from pandas import read_csv, Series, DataFrame, errors

# go to correct directory
os.chdir("bin/SF100")

# read directories for 100 chosen classes
dirFolders = set()
with open("100chosenClasses.txt", 'r') as file1:
    for item in file1:
        dirFolders.add(item.split('\t')[0])

# loop through each directory
"""
#frames = list()
for item in dirFolders:
    os.chdir(f"{item}/")
    print(os.listdir())
    try:
        stat = read_csv(
            filepath_or_buffer="evosuite-report/statistics.csv",
            delimiter=',')
        # frames.append(stat)
        # cat1 = stat['TARGET_CLASS']
        # print(stat.to_string())
    except (FileNotFoundError, errors.ParserError):
        pass
    os.chdir("../")"""

os.chdir("101_netweaver/evosuite-report/")
stat2 = read_csv("statistics.csv", delimiter=',')

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
# exceptionCoverageTimeline_list = [
#     f"ExceptionCoverageTimeline_T{num}" for num in range(1, 60)]
# weakMutationCoverageTimeline_list = [
#     f"WeakMutationCoverageTimeline_T{num}" for num in range(1, 60)]
# outputCoverageTimeline_list = [
#     f"OutputCoverageTimeline_T{num}" for num in range(1, 60)]
methodCoverageTimeline_list = [
    f"MethodCoverageTimeline_T{num}" for num in range(1, 60)]
# methodNoExceptionCoverageTimeline_list = [
#     f"MethodNoExceptionCoverageTimeline_T{num}" for num in range(1, 60)]
# cbranchCoverageTimeline_list = [
#     f"CBranchCoverageTimeline_T{num}" for num in range(1, 60)]
fitnessTimeline_list = [
    f"FitnessTimeline_T{num}" for num in range(1, 60)]
diversityTimeline_list = [
    f"DiversityTimeline_T{num}" for num in range(1, 60)]

# for item in stat2["TARGET_CLASS"].values:
#     print(item)

cat = stat2[stat2['TARGET_CLASS'] == 'com.sap.engine.services.dc.wsgate.Stop']
# print(cat)
row = cat[coverageTimeline_list]
row_mean = row.mean()
# print(row)
# print(row_mean)

row.plot(kind="box")
pyplot.show()
# row.plot()
# pyplot.show()
# coverageSeries = pandas.Series(
#    data=cat.squeeze(),
#    name="overallCoverage")
# coverageSeries.plot()
# pyplot.show()

# lineCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=lineCoverageTimeline_list,
#     name="lineCoverate")
# branchCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=branchCoverageTimeline_list,
#     name="branchCoverage")
# exceptionCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=exceptionCoverageTimeline_list,
#     name="exceptionCoverage")
# weakMutationCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=weakMutationCoverageTimeline_list,
#     name="weakMutationCoverage")
# outputCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=outputCoverageTimeline_list,
#     name="outputCoverage")
# methodCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=methodCoverageTimeline_list,
#     name="methodCoverage")
# methodNoExceptionCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=methodNoExceptionCoverageTimeline_list,
#     name="methodNoExceptionCoverage")
# cbranchCoverageSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=cbranchCoverageTimeline_list,
#     name="cbranchCoverage")
# fitnessSeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=fitnessTimeline_list,
#     name="fitness")
# diversitySeries = pandas.Series(
#     data=stat2.squeeze(),
#     index=diversityTimeline_list,
#     name="diversity")

# pickle all series'
# pandas.to_pickle()

# fitnessSeries.plot()
# diversitySeries.plot()
# pyplot.show()
