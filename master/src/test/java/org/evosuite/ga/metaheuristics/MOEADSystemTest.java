package org.evosuite.ga.metaheuristics;

import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
import org.evosuite.coverage.io.input.InputCoverageSuiteFitness;
import org.evosuite.coverage.line.LineCoverageSuiteFitness;
import org.evosuite.coverage.method.MethodCoverageSuiteFitness;
import org.evosuite.coverage.statement.StatementCoverageSuiteFitness;
import org.evosuite.ga.DummyChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class MOEADSystemTest extends SystemTestBase {

    @Test
    public void testEmptyPopulation() {
        MOEAD<DummyChromosome> ga = new MOEAD<>(null);

        assertTrue(ga.population.isEmpty());
    }

    /*
    @Test
    public void testInitialPopulation() {
        MOEAD<DummyChromosome> ga = new MOEAD<>(null);
        ga.initializePopulation();

    }
    */

    @Test
    public void testDistributedWeight3x10() throws IOException {
        // 10 by three dimensions (3 objectives)
        Properties.POPULATION = 10;
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        StatementCoverageSuiteFitness statement = new StatementCoverageSuiteFitness();

        MOEAD<TestSuiteChromosome> ga = new MOEAD<>(null);
        ga.addFitnessFunction(line);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(statement);
        Double[][] tmp = ga.distributeWeightVectors();

        for (Double[] a : tmp) {
         System.out.printf("%f, %f, %f%n", a[0], a[1], a[2]);
        }

        // assert # weights
        assertEquals(Properties.POPULATION, tmp.length);

        // assert weight values

        double[][] front = readFront("ILD_M3_H2_N10.pf", 3);
        assertEquals(front, tmp);

    }

    @Test
    public void testDistributedWeight3x19() throws IOException {
        // 19 by three dimensions (3 objectives)
        Properties.POPULATION = 19;
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        StatementCoverageSuiteFitness statement = new StatementCoverageSuiteFitness();

        MOEAD<TestSuiteChromosome> ga = new MOEAD<>(null);
        ga.addFitnessFunction(line);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(statement);
        Double[][] tmp = ga.distributeWeightVectors();

        for (Double[] a : tmp) {
            System.out.printf("%f, %f, %f%n", a[0], a[1], a[2]);
        }

        // assert # weights
        assertEquals(Properties.POPULATION, tmp.length);

        // assert weight values

        double[][] front = readFront("ILD_M3_H3_N19.pf", 3);
        assertEquals(front, tmp);

    }

    @Test
    public void testDistributedWeight3x31() throws IOException {
        // 31 by three dimensions (3 objectives)
        Properties.POPULATION = 31;
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        StatementCoverageSuiteFitness statement = new StatementCoverageSuiteFitness();

        MOEAD<TestSuiteChromosome> ga = new MOEAD<>(null);
        ga.addFitnessFunction(line);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(statement);
        Double[][] tmp = ga.distributeWeightVectors();

        for (Double[] a : tmp) {
            System.out.printf("%f, %f, %f%n", a[0], a[1], a[2]);
        }

        // assert # weights
        assertEquals(Properties.POPULATION, tmp.length);

        // assert weight values

        double[][] front = readFront("ILD_M3_H4_N31.pf", 3);
        assertEquals(front, tmp);

    }

    @Test
    public void testDistributedWeight5x126() throws IOException {
        // 126 by five dimensions (5 objectives)
        Properties.POPULATION = 126;
        LineCoverageSuiteFitness line = new LineCoverageSuiteFitness();
        BranchCoverageSuiteFitness branch = new BranchCoverageSuiteFitness();
        StatementCoverageSuiteFitness statement = new StatementCoverageSuiteFitness();
        MethodCoverageSuiteFitness method = new MethodCoverageSuiteFitness();
        InputCoverageSuiteFitness input = new InputCoverageSuiteFitness();

        MOEAD<TestSuiteChromosome> ga = new MOEAD<>(null);
        ga.addFitnessFunction(line);
        ga.addFitnessFunction(branch);
        ga.addFitnessFunction(statement);
        ga.addFitnessFunction(method);
        ga.addFitnessFunction(input);

        Double[][] tmp = ga.distributeWeightVectors();

        assertEquals(Properties.POPULATION, tmp.length);

        double[][] front = readFront("ILD_M5_H4_N126.pf", 5);
        assertEquals(front, tmp);
    }
    protected double[][] readFront(String name, int fronts) throws IOException{
        double[][] front = new double[Properties.POPULATION][fronts];
        int index = 0;

        InputStream in = ClassLoader.getSystemResourceAsStream(name);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] split = sCurrentLine.split(",");
            for (int i = 0; i < fronts; i++) {
                front[index][i] = Double.parseDouble(split[i]);

            }
            index++;
        }
        br.close();
        return front;
    }
}
