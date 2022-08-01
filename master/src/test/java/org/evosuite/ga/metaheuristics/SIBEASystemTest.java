package org.evosuite.ga.metaheuristics;

import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.NSGAChromosome;
import org.evosuite.ga.metaheuristics.sibea.HyperVolume;
import org.evosuite.ga.metaheuristics.sibea.SIBEA;
import org.evosuite.ga.operators.crossover.SBXCrossover;
import org.evosuite.ga.operators.selection.BinaryTournamentSelectionCrowdedComparison;
import org.evosuite.ga.problems.Problem;
import org.evosuite.ga.problems.metrics.Metrics;
import org.evosuite.ga.problems.multiobjective.FON;
import org.evosuite.ga.problems.multiobjective.ZDT1;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.*;

public class SIBEASystemTest extends SystemTestBase {
    @Before
    public void setUp() {
        Properties.POPULATION = 100;
        Properties.STOPPING_CONDITION = Properties.StoppingCondition.MAXGENERATIONS;
        Properties.SEARCH_BUDGET = 3;
        Properties.CROSSOVER_RATE = 0.9;
        Properties.RANDOM_SEED = 1L;
        Properties.MUTATION_RATE = 1d / 30d;
    }

    @Test
    public void testBasicHV() {
        // two objectives
        Problem<NSGAChromosome> p = new ZDT1();

        LinkedHashSet<FitnessFunction<NSGAChromosome>> fitnesses = new LinkedHashSet<>(p.getFitnessFunctions());
        FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);


        // 4 chromosomes
        NSGAChromosome t1 = new NSGAChromosome();
        NSGAChromosome t2 = new NSGAChromosome();
        NSGAChromosome t3 = new NSGAChromosome();
        NSGAChromosome t4 = new NSGAChromosome();

        NSGAChromosome ref = new NSGAChromosome();

        // create front [[1, 0], [0.5, 0.5], [0, 1], [1.5, 0.75]], ref [2,2]
        t1.addFitness(f1, 1.0);
        t1.addFitness(f2, 0.0);

        t2.addFitness(f1, 0.5);
        t2.addFitness(f2, 0.5);

        t3.addFitness(f1, 0.0);
        t3.addFitness(f2, 1.0);

        t4.addFitness(f1, 1.5);
        t4.addFitness(f2, 0.75);

        ref.addFitness(f1, 2.0);
        ref.addFitness(f2, 2.0);

        // create HV
        HyperVolume<NSGAChromosome> hv = new HyperVolume<>(fitnesses);

        // add chromosomes to population
        ArrayList<NSGAChromosome> pop = new ArrayList<>();
        pop.add(t1);
        pop.add(t2);
        pop.add(t3);
        pop.add(t4);

        hv.setReference(ref);

        assertEquals(3.25, hv.computeHV(pop), 0.0001);

    }

    @Test
    public void testHVZDT1() throws NumberFormatException, IOException {
        // load ZDT1
        Problem<NSGAChromosome> p = new ZDT1();

        // get its fitness functions
        final FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        final FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);

        // load ZDT1 front
        double[][] trueParetoFront = Metrics.readFront("ZDT1.pf");

        // convert to NSGAChromosomes
        ArrayList<NSGAChromosome> pop = new ArrayList<>();
        for (double[] front : trueParetoFront) {
            NSGAChromosome tmp = new NSGAChromosome();
            tmp.addFitness(f1, front[0]);
            tmp.addFitness(f2, front[1]);
            pop.add(tmp);
        }

        // instance of HyperVolume
        HyperVolume<NSGAChromosome> hv = new HyperVolume<>(new LinkedHashSet<>(p.getFitnessFunctions()));

        // create reference point (1.2, 1.2)
        NSGAChromosome ref = new NSGAChromosome();
        ref.addFitness(f1, 1.2);
        ref.addFitness(f2, 1.2);
        hv.setReference(ref);

        // compute HV for ZDT1
        assertEquals(1.0669653234149996, hv.computeHV(pop), 0.0000001);
    }

    @Test
    public void testHVFonseca() throws NumberFormatException, IOException {
        // load Fonseca
        Problem<NSGAChromosome> p = new FON();

        // get its fitness functions
        final FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        final FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);

        // load Fonseca front
        double[][] trueParetoFront = Metrics.readFront("Fonseca.pf");

        // convert to NSGAChromosomes
        ArrayList<NSGAChromosome> pop = new ArrayList<>();
        for (double[] front : trueParetoFront) {
            NSGAChromosome tmp = new NSGAChromosome();
            tmp.addFitness(f1, front[0]);
            tmp.addFitness(f2, front[1]);
            pop.add(tmp);
        }

        // instance of HyperVolume
        HyperVolume<NSGAChromosome> hv = new HyperVolume<>(new LinkedHashSet<>(p.getFitnessFunctions()));

        // create reference point (2, 2)
        NSGAChromosome ref = new NSGAChromosome();
        ref.addFitness(f1, 2.0);
        ref.addFitness(f2, 2.0);
        hv.setReference(ref);

        // compute HV for Fonseca
        assertEquals(3.3306458058939996, hv.computeHV(pop), 0.0000001);
    }

    @Test
    public void testHVSorter() {
        // load ZDT1
        Problem<NSGAChromosome> p = new ZDT1();
        LinkedHashSet<FitnessFunction<NSGAChromosome>> fitnesses = new LinkedHashSet<>(p.getFitnessFunctions());
        FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);

        // create HV
        HyperVolume<NSGAChromosome> hv = new HyperVolume<>(fitnesses);

        // create population
        NSGAChromosome t1 = new NSGAChromosome();
        NSGAChromosome t2 = new NSGAChromosome();
        NSGAChromosome t3 = new NSGAChromosome();
        NSGAChromosome t4 = new NSGAChromosome();

        // create front [[1, 0], [0.5, 0.5], [0, 1], [1.5, 0.75]], ref [2,2]
        t1.addFitness(f1, 1.0);
        t1.addFitness(f2, 0.0);

        t2.addFitness(f1, 0.5);
        t2.addFitness(f2, 0.5);

        t3.addFitness(f1, 0.0);
        t3.addFitness(f2, 1.0);

        t4.addFitness(f1, 1.5);
        t4.addFitness(f2, 0.75);

        // create ref-point
        NSGAChromosome ref = new NSGAChromosome();
        ref.addFitness(f1, 2.0);
        ref.addFitness(f2, 2.0);

        // set reference point
        hv.setReference(ref);

        List<NSGAChromosome> pop = new ArrayList<>();
        pop.add(t1);
        pop.add(t2);
        pop.add(t3);
        pop.add(t4);

        assertEquals(t1, pop.get(0));
        assertEquals(t2, pop.get(1));
        assertEquals(t3, pop.get(2));
        assertEquals(t4, pop.get(3));

        hv.HVSort(pop);

        // Order checked with jmetal
        assertEquals(t4, pop.get(0));
        assertEquals(t2, pop.get(1));
        assertEquals(t1, pop.get(2));
        assertEquals(t3, pop.get(3));
    }

    // Test integrations
    @Test
    public void testGenerateSolutionMethod() {
        Properties.POPULATION = 100;
        ChromosomeFactory<NSGAChromosome> factory = new RandomFactory(false, 30, 0.0, 1.0);
        GeneticAlgorithm<NSGAChromosome> ga = new SIBEA<>(factory);
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
        assertFalse(ga.population.isEmpty());
        assertEquals(Properties.POPULATION, ga.population.size());
    }
    @Test
    public void testEvolveMethod() throws IOException {
        ChromosomeFactory<NSGAChromosome> factory = new RandomFactory(false, 30, 0.0, 1.0);
        GeneticAlgorithm<NSGAChromosome> ga = new SIBEA<>(factory);
        BinaryTournamentSelectionCrowdedComparison<NSGAChromosome> ts =
                new BinaryTournamentSelectionCrowdedComparison<>();
        ts.setMaximize(false);
        ga.setSelectionFunction(ts);
        ga.setCrossOverFunction(new SBXCrossover());

        // set up two goals
        Problem<NSGAChromosome> p = new ZDT1();
        final FitnessFunction<NSGAChromosome> f1 = p.getFitnessFunctions().get(0);
        final FitnessFunction<NSGAChromosome> f2 = p.getFitnessFunctions().get(1);
        ga.addFitnessFunction(f1);
        ga.addFitnessFunction(f2);

        // load "BasicTwoTestFronts"
        Properties.POPULATION = 6;
        double[][] fullFront = Metrics.readFront("BasicTwoTestFronts.pf");
        for (double[] indiv : fullFront) {
            NSGAChromosome tmp = new NSGAChromosome();
            tmp.addFitness(f1, indiv[0]);
            tmp.addFitness(f2, indiv[1]);
            ga.population.add(tmp);
        }
        Properties.POPULATION = 4;

        // execute
        ga.evolve();

        // asserts
        assertEquals(Properties.POPULATION, ga.population.size());
    }

    @Test
    public void testMateMethod() {
        Properties.POPULATION = 10;
        ChromosomeFactory<NSGAChromosome> factory = new RandomFactory(false, 30, 0.0, 1.0);
        SIBEA<NSGAChromosome> ga = new SIBEA<>(factory);
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

        ga.initializePopulation();
        assertEquals(Properties.POPULATION, ga.population.size());

        ga.mate();
        assertEquals(Properties.POPULATION*2, ga.population.size());
    }
}
