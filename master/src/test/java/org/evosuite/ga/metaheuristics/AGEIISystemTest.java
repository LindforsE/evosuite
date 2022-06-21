package org.evosuite.ga.metaheuristics;

import org.evosuite.SystemTestBase;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
import org.evosuite.coverage.line.LineCoverageSuiteFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.operators.ranking.RankBasedPreferenceSorting;
import org.evosuite.ga.operators.ranking.RankingFunction;
import org.evosuite.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anySet;

// import java.util.ArrayList;
// import java.util.List;

public class AGEIISystemTest extends SystemTestBase {
    // Test for increment() method
    @Test
    public void testIncrementMethod() {
        // fitness and algorithm
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        AGEII<TestSuiteChromosome> ga = new AGEII<>(null);
        ga.addFitnessFunction(branch);

        // chromosome
        TestSuiteChromosome t1 = new TestSuiteChromosome();
        t1.addFitness(branch, 0.3);

        // increement 0.3 to 1.3
        ga.increment(t1);

        assertEquals(1.3, t1.getFitness(branch), 0.01);
    }

    @Test
    public void testFloorMethod() {
        // set precision
        Properties.EPSILON = 0.1;
        
        // create fitness
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();

        // create algorithm
        AGEII<TestSuiteChromosome> ga = new AGEII<>(null);
        ga.addFitnessFunction(branch);

        // create test chromosome
        TestSuiteChromosome t1 = new TestSuiteChromosome();
        t1.addFitness(branch, 0.33333);

        // floor 0.33333 to 0.3
        ga.floor(t1);
        assertEquals(0.3, t1.getFitness(branch), 0.01);

        // change precision
        Properties.EPSILON = 0.2;

        // new fitness
        t1.setFitness(branch, 0.33333);

        // floor 0.33333 to 0.2
        ga.floor(t1);
        assertEquals(0.2, t1.getFitness(branch), 0.01);

        // change precision
        Properties.EPSILON = 0.5;

        // new fitness
        t1.setFitness(branch, 0.7);

        // floor 0.7 to 0.5
        ga.floor(t1);
        assertEquals(0.5, t1.getFitness(branch), 0.01);

        // new fitness
        t1.setFitness(branch, 2.3);

        // floor 2.3 to 2.0
        ga.floor(t1);
        assertEquals(2.0, t1.getFitness(branch), 0.01);

    }

    // test for floorAndInsert() method
    @Test
    public void testFloorAndInsertMethod() {
        Properties.EPSILON = 0.1;
        // coverages
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        
        // algorithm
        AGEII<TestSuiteChromosome> ga = new AGEII<>(null);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(line);

        // first chromosome
        TestSuiteChromosome t1 = new TestSuiteChromosome();
        t1.addFitness(branch, 0.5);
        t1.addFitness(line, 0.3);

        // clones of first chromosome (to use later)
        TestSuiteChromosome t1c = t1.clone();

        // add first chromosome to temporary list
        List<TestSuiteChromosome> tmp = new ArrayList<TestSuiteChromosome>();
        tmp.add(t1);

        ga.floorAndInsert(tmp);

        // assert that it got inserted
        assertFalse(ga.approxArchive.isEmpty());

        // create a temporary floored list
        List<TestSuiteChromosome> floored_tmp = new ArrayList<TestSuiteChromosome>();
        
        // add floored chromosome to floored list
        floored_tmp.add(t1);
        
        // assert that it was floored correctly
        assertTrue(floored_tmp.containsAll(ga.approxArchive) && ga.approxArchive.containsAll(floored_tmp));

       
        // try flooring the previous chromosome again
        tmp.clear();
        tmp.add(t1c);
        ga.floorAndInsert(tmp);
        
        // assert that duplicate was not inserted
        assertTrue(floored_tmp.containsAll(ga.approxArchive) && ga.approxArchive.containsAll(floored_tmp));

        // Create a new chromosome with better fitness
        TestSuiteChromosome t2 = new TestSuiteChromosome();
        t2.addFitness(branch, 0.4);
        t2.addFitness(line, 0.2);
        
        // try to add it to the list 
        tmp.clear();
        tmp.add(t2);
        ga.floorAndInsert(tmp);

        floored_tmp.clear();
        floored_tmp.add(t2);
        
        // assert that dominating was inserted, and previous was removed
        assertTrue(floored_tmp.containsAll(ga.approxArchive) && ga.approxArchive.containsAll(floored_tmp));


        // add a new chromosome with worse fitness
        TestSuiteChromosome t3 = new TestSuiteChromosome();
        t3.addFitness(branch, 1.0);
        t3.addFitness(line, 1.0);


        // try to add it to the list
        tmp.clear();
        tmp.add(t3);
        ga.floorAndInsert(tmp);

        // assert that worse individual was not inserted, an previous non-dominated remains
        assertTrue(floored_tmp.containsAll(ga.approxArchive) && ga.approxArchive.containsAll(floored_tmp));
    }

    // test for update loop
}
