package org.evosuite.ga.metaheuristics.sibea;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.AGEII;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SIBEA<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    // variables
    private static final long serialVersionUID = 4L;
    private static final Logger logger = LoggerFactory.getLogger(AGEII.class);

    private HyperVolume<T> HV;


    /**
     * Constructor
     *
     * @param factory a {@link ChromosomeFactory} object.
     */
    public SIBEA(ChromosomeFactory<T> factory) {
        super(factory);
        HV = new HyperVolume<T>(new LinkedHashSet<FitnessFunction<T>>(getFitnessFunctions()));
    }

    /** {@inheritDoc} */
    @Override
    protected void evolve() {
        // (environmental selection) Iterate the following three steps until the
        // size of the population does no longer exceed mu
        if (population.size() > Properties.POPULATION) {
            // 1. Rank the population using Pareto Dominance and determine
            // the set of individuals with the worst rank (P').
            // this.population.sort(new DominanceComparator<>()); ???
            rankingFunction.computeRankingAssignment(population, new LinkedHashSet<FitnessFunction<T>>(getFitnessFunctions()));
            int fronts = rankingFunction.getNumberOfSubfronts();
            List<T> worst = rankingFunction.getSubfront(fronts--);

            // while difference is larger than size of worst front, remove all.
            while (worst.size() >= (population.size() - Properties.POPULATION)) {
                population.removeAll(worst);
                worst = rankingFunction.getSubfront(fronts--);
            }

            // 2. For each solution x from the worst rank P', determine
            // the loss d(x) with regard to the indicator I if it is removed from P'
            // i.e. d(x) := I(P') - I(P'\{x})
            HV.HVSort(population);

            // 3. Remove the solution with the smallest loss d(x) from
            // the population P (ties are broken randomly).
            int difference = population.size() - Properties.POPULATION;
            if (difference >= 1)
                population.removeAll(worst.subList(0, difference - 1));
        }
        this.currentIteration++;
    }

    /**
     * Code for the mating step goes here.
     */
    protected void mate() {
        // Randomly select elements from P to form a temporary mating pool Q of size mu.
        ArrayList<T> matingPop = new ArrayList<>(this.population);
        for (int i = 0; i < Properties.POPULATION/2; i++) {
            // Apply variation operators such as recombination and mutation to mating pool Q to yield Q'.
            T p1 = Randomness.choice(population);
            T p2 = Randomness.choice(population);

            T q1 = p1.clone();
            T q2 = p2.clone();

            // Apply crossover and mutation
            try {
                if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE)
                    crossoverFunction.crossOver(q1, q2);
            } catch (Exception e) {
                logger.info("Crossover failed.");
            }
            if (Randomness.nextDouble() <= Properties.MUTATION_RATE) {
                notifyMutation(q1);
                q1.mutate();
                notifyMutation(q2);
                q2.mutate();
            }

            // Evaluate for each fitness function
            for (final FitnessFunction<T> ff : this.getFitnessFunctions()) {
                ff.getFitness(q1);
                notifyEvaluation(q1);
                ff.getFitness(q2);
                notifyEvaluation(q2);
            }

            // add to mating pool population (making it Q')
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

        // initialize population with random individuals
        this.generateInitialPopulation(Properties.POPULATION);

        // Generate an initial set of decision vectors P of size mu,

        // set the generation counter m := 0.
        currentIteration = 0;

        // Notify
        this.notifyIteration();
    }

    /** {@inheritDoc} */
    @Override
    public void generateSolution() {
        logger.info("executing generateSolution function");

        // 1. Initialization
        if (population.isEmpty()) {
            this.initializePopulation();
        }
        // 2. Environmental selection
        this.evolve();
        this.notifyIteration();

        // 3. Check termination
        // If m >= N then set A := P and stop.
        // otherwise set m := m + 1.
        while (!isFinished()) {
            // 4. Mating
            this.mate();

            this.evolve();
            this.notifyIteration();

            // Where to put this?????
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

