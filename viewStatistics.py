from cProfile import label
import os
import numpy as np
from matplotlib import pyplot
from pandas import read_csv

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

# Extract certain class (rows)
cat = stat2[stat2['TARGET_CLASS'] == 'com.sap.engine.services.dc.wsgate.Stop']
# print(cat)

# Extract specific algorithms (rows)
# rnd_stats = cat[cat['algorithm'] == 'RANDOM_SEARCH']
nsga_stats = cat[cat['algorithm'] == 'NSGAII']

# Extract specific coverage-type (column)
# rnd_cov = rnd_stats[coverageTimeline_list]
nsga_cov = nsga_stats[coverageTimeline_list]
print(nsga_cov)

# Plot as series (transpose) ??
# Plot as line (median with std)
"""
nsga_errors = nsga_cov.std()
nsga_median = nsga_cov.median()
ax = nsga_median.plot(kind='line',
                      yerr=nsga_errors,
                      use_index=False,
                      yticks=np.arange(0.0, 1.0, 0.1),
                      xlabel='seconds',
                      ylabel='coverage')
"""

# Plot as box-plot
"""ax = nsga_cov.plot(kind='box',
                   use_index=False,
                   xlabel='seconds',
                   grid=True,
                   yticks=np.arange(0.0, 1.0, 0.1),
                   ylabel='coverage')
"""

# Show the plot(s)
pyplot.show()

# print(cat)

# row1 = nsga_stats[coverageTimeline_list]
# row2 = rnd_stats[coverageTimeline_list]

# MY OLD WAY OF PLOTTING
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
