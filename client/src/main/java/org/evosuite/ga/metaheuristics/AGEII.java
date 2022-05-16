package org.evosuite.ga.metaheuristics;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.evosuite.utils.Randomness;
import org.evosuite.ga.archive.Archive;

// import org.evosuite.ga.archive.Archive;

// these can be replaced with "import java.util.*;"
import java.util.List;
import java.util.ArrayList;

/**
 * AGE-II implementation
 * <INSERT ARTICLE HERE>
 * 
 * @author Elias
 */
public class AGEII<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    // variables
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AGEII.class);

    // epsilon-grid variable
    // Properties.EPSILON

    // epsilon (approximate) archive

    // Constructor
    public AGEII(ChromosomeFactory<T> factory) {
        super(factory);
    }

    @Override
    protected void evolve() {
        // P = Population
        // O = offspring population
        // mu = population size?
        // lambda = ?????
        // Aeg = approximative archive
        // epsilon-grid = ????

        // initialize empty offspring population;
        List<T> offspringPopulation = new ArrayList<>(Properties.POPULATION);

        // for j <- 1 to lambda, do;
        // WHAT IS LAMBDA? (mu is Population size, lambda is ???)
        for (int i = 0; i < 1; i++) {
            // Select two individuals from Population;
            T parent1 = this.selectionFunction.select(population);
            T parent2 = this.selectionFunction.select(population);
            T offspring1 = parent1.clone();
            T offspring2 = parent2.clone();
            // Apply crossover and mutation;
            try {
                if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE)
                    this.crossoverFunction.crossOver(offspring1, offspring2);
            } catch (Exception e) {
                logger.info("Crossover failed.");
            }
            if (Randomness.nextDouble() <= Properties.MUTATION_RATE) {
                notifyMutation(offspring1);
                offspring1.mutate();
                notifyMutation(offspring2);
                offspring2.mutate();
            }
            // Add new individuals to offspring population;
            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }

        // foreach individual in offspring population, do
        for (int i = 0; i < offspringPopulation.size(); i++) {
            // Insert offspring this.floor(p) in the approximative archive Aeg such that
            // only non-dominated solutions remain;
            // Discard offspring p if it's dominated by any point;
            // this.increment(a), where a is part of A;
        }

        // Add offsprings to Population, P <-P union O;
        this.population = union(this.population, offspringPopulation);

        // While abs(Population) > mu, do;
        while (this.population.size() > Properties.POPULATION) {
            // Remove individual from Population that is of least importance to the
            // approximation;
            // this should be a ranking function, right?
        }
    }

    /**
     * Algorithm 2
     * 
     * @param input d-dimensional objective vector x, archive parameter eg
     * @return Corresponding vector v on the epsilon-grid
     */
    private void floor(T input) {
        // for i = 1 to d do v[i] <- x[i]/eg;
    }

    /**
     * Algorithm 3
     * 
     * @param input d-dimensional vector x, archive parameter eg
     * @return Corresponding vector v that has each of its components increased by 1
     */
    private void increment(T input) {
        // for i = 1 to d do v[i] <- o[i]+1;
    }

    @Override
    public void generateSolution() {
        // STUB
    }

    @Override
    public void initializePopulation() {
        // initialize population with random individuals;
        this.generateInitialPopulation(Properties.POPULATION);

        // set grid resolution of the approximate archive;

        // foreach individual in Population, do;
        for (int i = 0; i < this.population.size(); i++) {
            // Insert offspring floor(p) in the approximative archive Aeg such that only
            // non-dominated solutions remain;
            // this.floor(this.population.get(i));
            // Archive.getArchiveInstance()
        }
    }

    // Union function
    protected List<T> union(List<T> population, List<T> offspringPopulation) {
        int newSize = population.size() + offspringPopulation.size();
        if (newSize < Properties.POPULATION)
            newSize = Properties.POPULATION;

        // Create a new population;
        List<T> union = new ArrayList<>(newSize);
        union.addAll(population);

        for (int i = population.size(); i < (population.size() + offspringPopulation.size()); i++)
            union.add(offspringPopulation.get(i - population.size()));

        return union;
    }

}
