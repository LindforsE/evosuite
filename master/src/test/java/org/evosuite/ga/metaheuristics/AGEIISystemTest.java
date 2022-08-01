package org.evosuite.ga.metaheuristics;

import org.evosuite.Properties;
import org.evosuite.Properties.StoppingCondition;
import org.evosuite.SystemTestBase;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
import org.evosuite.coverage.line.LineCoverageSuiteFitness;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.NSGAChromosome;
import org.evosuite.ga.comparators.DominanceComparator;
import org.evosuite.ga.operators.crossover.SBXCrossover;
import org.evosuite.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.evosuite.ga.problems.Problem;
import org.evosuite.ga.problems.multiobjective.ZDT1;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

// import java.util.ArrayList;
// import java.util.List;

public class AGEIISystemTest extends SystemTestBase {
    @Before
    public void setUp() {
        Properties.POPULATION = 3;
		Properties.CROSSOVER_RATE = 0.9;
		Properties.RANDOM_SEED = 1L;
        Properties.STOPPING_CONDITION = Properties.StoppingCondition.MAXGENERATIONS;
        Properties.SEARCH_BUDGET = 250;
	}
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
        assertEquals(0.3, t1.getFitness(branch), 0.0001);

        // change precision
        Properties.EPSILON = 0.2;

        // new fitness
        t1.setFitness(branch, 0.33333);

        // floor 0.33333 to 0.2
        ga.floor(t1);
        assertEquals(0.2, t1.getFitness(branch), 0.0001);

        // change precision
        Properties.EPSILON = 0.5;

        // new fitness
        t1.setFitness(branch, 0.7);

        // floor 0.7 to 0.5
        ga.floor(t1);
        assertEquals(0.5, t1.getFitness(branch), 0.0001);

        // new fitness
        t1.setFitness(branch, 2.3);

        // floor 2.3 to 2.0
        ga.floor(t1);
        assertEquals(2.0, t1.getFitness(branch), 0.0001);

    }

    // test for floorAndInsert() method
    @Test
    public void testFloorAndInsertMethod() {
        Properties.EPSILON = 0.1;
        // coverages
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        
        DominanceComparator<TestSuiteChromosome> comparator = new DominanceComparator<>();
        
        // algorithm
        AGEII<TestSuiteChromosome> ga = new AGEII<>(null);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(line);

        // first chromosome
        TestSuiteChromosome t1 = new TestSuiteChromosome();
        t1.addFitness(branch, 0.5);
        t1.addFitness(line, 0.3);

        // clones of first chromosome (to use later)
        TestSuiteChromosome tComp = t1.clone();

        // add first chromosome to temporary list
        List<TestSuiteChromosome> tmp = new ArrayList<TestSuiteChromosome>();
        tmp.add(t1);

        ga.floorAndInsert(tmp);

        // assert that it got inserted
        assertEquals(1, ga.approxArchive.size());
        
        // assert that it was floored correctly
        TestSuiteChromosome tFloored = ga.approxArchive.get(0);
        assertEquals(0.5, tFloored.getFitness(branch), 0.0001);
        assertEquals(0.3, tFloored.getFitness(line), 0.0001);
        
        // assert that it's non-dominated (equal) with what was just put in
        assertEquals(0, comparator.compare(ga.approxArchive.get(0), tComp));

        // try flooring the previous chromosome again
        ga.floorAndInsert(tmp);
        
        // assert that duplicate was not inserted
        assertEquals(1, ga.approxArchive.size());

        // assert that value is still correct
        tFloored = ga.approxArchive.get(0);
        assertEquals(0.5, tFloored.getFitness(branch), 0.0001);
        assertEquals(0.3, tFloored.getFitness(line), 0.0001);
        

        // Create a new chromosome with better fitness
        TestSuiteChromosome t2 = new TestSuiteChromosome();
        t2.addFitness(branch, 0.4);
        t2.addFitness(line, 0.2);
        
        // try to add it to the list 
        tmp.clear();
        tmp.add(t2);
        ga.floorAndInsert(tmp);

        tComp.setFitness(branch, 0.4);
        tComp.setFitness(line, 0.2);
        
        // assert that dominating was inserted, and previous was removed
        assertEquals(1, ga.approxArchive.size());

        // assert that its values are correct
        tFloored = ga.approxArchive.get(0);
        assertEquals(0.4, tFloored.getFitness(branch), 0.0001);
        assertEquals(0.2, tFloored.getFitness(line), 0.0001);


        // add a new chromosome with worse fitness
        TestSuiteChromosome t3 = new TestSuiteChromosome();
        t3.addFitness(branch, 1.0);
        t3.addFitness(line, 1.0);


        // try to add it to the list
        tmp.clear();
        tmp.add(t3);
        ga.floorAndInsert(tmp);

        // assert that worse individual was not inserted, an previous non-dominated remains
        assertEquals(1, ga.approxArchive.size());
        
        // assert that its values are still correct
        tFloored = ga.approxArchive.get(0);
        assertEquals(0.4, tFloored.getFitness(branch), 0.0001);
        assertEquals(0.2, tFloored.getFitness(line), 0.0001);
        
        tComp.setFitness(branch, 1.0);
        tComp.setFitness(line, 1.0);
    }

    // test Generate solution
    @Test
    public void testGenerateSolutionMethod() {
        Properties.MUTATION_RATE = 1d / 10d;
        Properties.POPULATION = 8;
        Properties.SEARCH_BUDGET = 8;
        Properties.STOPPING_CONDITION = StoppingCondition.MAXGENERATIONS;
        Properties.CROSSOVER_RATE = 0.9;
        Properties.RANDOM_SEED = 1L;
        ChromosomeFactory<NSGAChromosome> factory = new RandomFactory(false, 30, 0.0, 1.0);

        GeneticAlgorithm<NSGAChromosome> ga = new AGEII<>(factory);
        BinaryTournamentSelectionCrowdedComparison<NSGAChromosome> ts =
            new BinaryTournamentSelectionCrowdedComparison<>();
        ts.setMaximize(false);
        ga.setSelectionFunction(ts);
        ga.setCrossOverFunction(new SBXCrossover());

        Problem<NSGAChromosome> p = new ZDT1();
        final FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        final FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);
        ga.addFitnessFunction(f1);
        ga.addFitnessFunction(f2);

        // execute
        ga.generateSolution();
        assertEquals(Properties.POPULATION, ga.population.size());
        //assertFalse(ga.population.isEmpty());
    }

    // test the integration
    /*
    @Test
    public void testIntegration() {
        // Properties
        Properties.MUTATION_RATE = 1d / 1d;
	    Properties.CRITERION = new Criterion[2];
        Properties.CRITERION[0] = Criterion.RHO;
        Properties.CRITERION[1] = Criterion.AMBIGUITY;
	    Properties.ALGORITHM = Algorithm.AGEII;
	    Properties.SELECTION_FUNCTION = Properties.SelectionFunction.BINARY_TOURNAMENT;
	    Properties.MINIMIZE = false;
	    Properties.INLINE = false;
	    Properties.STOP_ZERO = false;
	    Properties.RANKING_TYPE = Properties.RankingType.FAST_NON_DOMINATED_SORTING;

        // Evosuite
        EvoSuite evosuite = new EvoSuite();

        // CUT (class under test)
        String targetClass = Calculator.class.getCanonicalName();
        Properties.TARGET_CLASS = targetClass;

        // commands to use
        String[] command = new String[] {
            "-generateSuite",
            "-class", targetClass
        };

        // start
        Object result = evosuite.parseCommandLine(command);
        assertNotNull(result);
    }
    */
}
