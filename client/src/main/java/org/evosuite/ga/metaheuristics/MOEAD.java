package org.evosuite.ga.metaheuristics;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MOEA/D implementation
 * <INSERT ARTICLE HERE>
 * 
 * @author Elias
 */
public class MOEAD<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MOEAD.class);

    public MOEAD(ChromosomeFactory<T> factory) {
        super(factory);
    }

    @Override
    protected void evolve() {
        // N = Number of sub (decomposed) problems
        // for i= 1, ..., N, do
        for (int i = 0; i < 1; i++) {

            // step 2.1 Reproduction
            // randomly select two indexes, k,l from B(i),
            // and then generate a new solution y
            // from xk and xl by using genetic operators
            T parent1 = selectionFunction.select(population);
            T parent2 = selectionFunction.select(population);

            T offspring1 = parent1.clone();
            T offspring2 = parent2.clone();

            // crossover can fail, put inside try catch.
            try {
                crossoverFunction.crossOver(offspring1, offspring2);
            } catch (Exception e) {
                logger.info("Crossover failed.");
            }

            // step 2.2 Improvement
            // Apply a problem-specific repair/improvement heuristic on y to produce y'

            // step 2.3 Update z
            // ...

            // step 2.4 Update Neighboring Solutions
            // ...

            // step 2.5 Update EP
            // ...
        }
    }

    @Override
    public void generateSolution() {
        logger.info("executing generateSolution function");

        if (population.isEmpty())
            initializePopulation();

        // while !stoppingCriteria
        while (!isFinished()) {
            // update (go to step 2)
            evolve();
            this.notifyIteration();
            this.writeIndividuals(this.population);
        }
        // finished
        notifySearchFinished();
    }

    @Override
    public void initializePopulation() {
        logger.info("executing initializePopulation function");

        // INITIALIZATION
        // step 1.1
        // EP = empty set

        // step 1.2
        // Compute the Euclidean distances between any
        // two weight vectors and then work out the closest weight
        // vectors to each weight vector

        // step 1.3
        // Generate an initial population
        // randomly or by a problem-specific method.

        // step 1.4
        // Initialize by a problem-specific method.
    }
}
