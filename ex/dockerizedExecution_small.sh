#!/bin/bash
cd ../bin/SF100
cd 84_ifx-framework
docker run --rm -it --cpus=4 -u 1000 -v $PWD:/evosuite evosuite/evosuite:latest-java-8 -generateSuite -Dtimeline_interval=5000 -Dstopping_condition=MAXGENERATIONS -Dsearch_budget=3 -Dglobal_timeout=8000 -Dshow_progress=false -Dignore_missing_statistics=true -criterion=BRANCH:LINE:STATEMENT:METHOD -Doutput_variables=TARGET_CLASS,algorithm,criterion,Total_Time,Size,Length,Tests_Executed,Fitness_Evaluations,Generations,Total_Goals,Covered_Goals,Lines,Covered_Lines,Total_Methods,Covered_Methods,Branchless_Methods,Covered_Branchless_Methods,Coverage,LineCoverage,BranchCoverage,MethodCoverage,StatementCoverage,Fitness -class org.sourceforge.ifx.framework.element.PassbkItemDetail -Dalgorithm=SIBEA
echo 84_ifx-framework completed