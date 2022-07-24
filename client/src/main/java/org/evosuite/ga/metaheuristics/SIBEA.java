package org.evosuite.ga.metaheuristics;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.DominanceComparator;
import org.evosuite.ga.operators.ranking.RankingFunction;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class SIBEA<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    // variables
    private static final long serialVersionUID = 4L;
    private static final Logger logger = LoggerFactory.getLogger(AGEII.class);

    /**
     * Constructor
     *
     * @param factory a {@link ChromosomeFactory} object.
     */
    public SIBEA(ChromosomeFactory<T> factory) {
        super(factory);
    }

    /** {@inheritDoc} */
    @Override
    protected void evolve() {

        // (environmental selection) Iterate the following three steps until the
        // size of the population does no longer exceed mu
        while (population.size() < Properties.POPULATION) {
            // 1. Rank the population using Pareto Dominance and determine
            // the set of individuals with the worst rank (P').
            // this.population.sort(new DominanceComparator<>()); ???
            rankingFunction.computeRankingAssignment(population, new LinkedHashSet<FitnessFunction<T>>(getFitnessFunctions()));
            List<T> worst = rankingFunction.getSubfront(rankingFunction.getNumberOfSubfronts());

            // 2. For each solution x from the worst rank P', determine
            // the loss d(x) w.r.t. the indicator I if it is removed from P'
            // i.e. d(x) := I(P') - I(P'\{x})

            // 3. Remove the solution with the smallest loss d(x) from
            // the population P (ties are broken randomly).
        }

        // check termination. If m >= N then set A := P and stop.
        // otherwise set m := m + 1.
        this.currentIteration++;

        // (Mating) Randomly select elements from P to form a temporary mating pool Q of size mu.
        ArrayList<T> matingPop = new ArrayList<>(this.population);
        for (int i = 0; i < Properties.POPULATION/2; i++) {
            // Apply variation operators such as recombination and mutation to mating pool Q to yield Q'.
            T q1 = Randomness.choice(population);
            T q2 = Randomness.choice(population);
            matingPop.add(q1);
            matingPop.add(q2);
        }
        // Set P := P + Q' (multi-set union) and continue with environmental selection.
        this.union(population, matingPop);
    }

    /** {@inheritDoc} */
    @Override
    public void initializePopulation() {
        logger.info("executing initializePopulation function");
        notifySearchStarted();
        currentIteration = 0;

        // initialize population with random individuals
        this.generateInitialPopulation(Properties.POPULATION);

        // Generate an initial set of decision vectors P of size mu,
        // set the generation counter m := 0.

        // Notify
        this.notifyIteration();
    }

    /** {@inheritDoc} */
    @Override
    public void generateSolution() {
        logger.info("executing generateSolution function");

        if (population.isEmpty()) {
            this.initializePopulation();
        }

        while (!isFinished()) {
            this.evolve();
            this.notifyIteration();
            this.writeIndividuals(this.population);
        }

        notifySearchFinished();
    }
    /** Union function. */
    protected List<T> union(List<T> population, List<T> offspringPopulation) {
        int newSize = population.size() + offspringPopulation.size();
        if (newSize < Properties.POPULATION)
            newSize = Properties.POPULATION;

        // Create a new population
        List<T> union = new ArrayList<>(newSize);
        union.addAll(population);

        for (int i = population.size(); i < (population.size() + offspringPopulation.size()); i++)
            union.add(offspringPopulation.get(i - population.size()));

        return union;
    }
}

