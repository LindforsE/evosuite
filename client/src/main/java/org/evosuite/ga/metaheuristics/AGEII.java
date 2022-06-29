package org.evosuite.ga.metaheuristics;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
// these can be replaced with "import java.util.*;"
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.RankAndCrowdingDistanceComparator;
import org.evosuite.ga.comparators.DominanceComparator;
import org.evosuite.utils.Randomness;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AGE-II implementation <INSERT ARTICLE HERE>
 * 
 * @author Elias
 */
public class AGEII<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    // variables
    private static final long serialVersionUID = 4L;
    private static final Logger logger = LoggerFactory.getLogger(AGEII.class);

    private final DominanceComparator<T> comparator;

    // epsilon-grid variable
    // Properties.EPSILON (default value should be 0.1)

    // epsilon (approximate) archive
    final List<T> approxArchive;

    // Constructor
    public AGEII(ChromosomeFactory<T> factory) {
        super(factory);
        this.comparator = new DominanceComparator<>();
        this.approxArchive = new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    protected void evolve() {
        // P = Population
        // O = offspring population
        // mu = population size?
        // lambda = ?????
        // Aeg = approximative archive
        // default epsilon = 0.1 ??

        // initialize empty offspring population
        List<T> offspringPopulation = new ArrayList<>(population.size());

        // for j <- 1 to lambda, do
        // WHAT IS LAMBDA? (mu is Population size, lambda is ???)
        for (int i = 0; i < (this.population.size() / 2); i++) {
            // Select two individuals from Population
            T parent1 = selectionFunction.select(population);
            T parent2 = selectionFunction.select(population);
            T offspring1 = parent1.clone();
            T offspring2 = parent2.clone();

            // Apply crossover and mutation
            try {
                if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE)
                    crossoverFunction.crossOver(offspring1, offspring2);
            } catch (Exception e) {
                logger.info("Crossover failed.");
            }
            if (Randomness.nextDouble() <= Properties.MUTATION_RATE) {
                notifyMutation(offspring1);
                offspring1.mutate();
                notifyMutation(offspring2);
                offspring2.mutate();
            }

            // Evaluate for each fitness function
            for (final FitnessFunction<T> ff : this.getFitnessFunctions()) {
                ff.getFitness(offspring1);
                notifyEvaluation(offspring1);
                ff.getFitness(offspring2);
                notifyEvaluation(offspring2);
            }

            // Add new individuals to offspring population
            offspringPopulation.add(offspring1);
            offspringPopulation.add(offspring2);
        }

        /// foreach individual in offspring population, do
        /// Insert offspring this.floor(p) in the approximative archive Aeg such that
        /// only non-dominated solutions remain
        floorAndInsert(offspringPopulation);
        // (Insert only if floor(p) is non-nominating?)
        // (Insert all, then keep only best front?)
        
        
        /// Discard offspring p if it's dominated by any point
        /// this.increment(a), where a is part of A (Aeg)

        // create temp incremental-list
        List<T> incList = createIncList();

        // for-each item in offspring
        Iterator<T> it = offspringPopulation.iterator();
        while (it.hasNext()) {
            T item = it.next();
            // if any individual from incList dominates the item, remove it.
            for (T over : incList) {
                if (comparator.compare(item, over) == 1) {
                    it.remove();
                    break;
                }
            }    
        }

        // Add offsprings to Population, P <-P union O
        population = union(this.population, offspringPopulation);

        // While abs(Population) > mu, do
        if (this.population.size() > Properties.POPULATION) {
            // Remove individual from Population that is of least importance to the
            // approximation
            // (this should be a ranking function, right?)

            // IS THIS CORRECT? (sort according to rank and crowding distance)
            this.population.sort(new RankAndCrowdingDistanceComparator<>(true));
            // IS THIS CORRECT? (walk through individuals which are outside range)
            for (int i = this.population.size(); i < Properties.POPULATION; i++)
                // IS THIS CORRECT? (remove individuals)
                this.population.remove(i);
        }
        // (Update interate counter)
        
        this.currentIteration++;

    }

    /**
     * Algorithm 1
     * 
     * @param input d-dimensional objective vector x, archive parameter eg
     * @return Corresponding vector v on the epsilon-grid
     */
    protected void floor(T input) {
        // for i = 1 to d do v[i] <- x[i]/epsilon
        for (final FitnessFunction<T> ff : getFitnessFunctions()) {

            // Do math with BigDecimal
            BigDecimal a = BigDecimal.valueOf(input.getFitness(ff));
            a = a.multiply(getEpsilon(), MathContext.UNLIMITED);
            a = a.setScale(0, RoundingMode.FLOOR);
            a = a.divide(getEpsilon());

            // insert back as double
            if (!Double.isInfinite(a.doubleValue()))
                input.setFitness(ff, a.doubleValue());
        }
    }
    /**
     * Get 1 divided by Properties.EPSILON
     * @return BigDecimal value of 1/Properties.EPSILON
     */
    protected BigDecimal getEpsilon() {
        return BigDecimal.valueOf(1).divide(BigDecimal.valueOf(Properties.EPSILON));
    }

    /**
     * Inserts non-dominating floor(p) from input into Aeg
     * @param input List of objectives
     */
    protected void floorAndInsert(List<T> input) {
        // floor all individuals
        for (T item : input) {
            T tmp = item.clone();
            floor(tmp);
            // insert only if individual is not already in list
            if (!approxArchive.contains(tmp)) {
                approxArchive.add(tmp);
            }
        }
        // sort (preferably "FastNonDominatedSorting")
        rankingFunction.computeRankingAssignment(this.approxArchive, new LinkedHashSet<FitnessFunction<T>>(getFitnessFunctions()));
        
        // retain only the non-dominating individuals
        approxArchive.retainAll(rankingFunction.getSubfront(0));
    }

    /**
     * Algorithm 2
     * 
     * @param input d-dimensional vector x, archive parameter eg
     * @return Corresponding vector v that has each of its components increased by 1
     */
    protected void increment(T input) {
        // for i = 1 to d do v[i] <- o[i]+1
        for (final FitnessFunction<T> ff : this.getFitnessFunctions()) {
            input.setFitness(ff, (input.getFitness(ff) + 1));
        }
    }

    protected List<T> createIncList() {
        List<T> incedList = new ArrayList<>(approxArchive.size());
        for (T item : approxArchive) {
            T tmp = item.clone();
            increment(tmp);
            incedList.add(tmp);
        }
        return incedList;
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

    /** {@inheritDoc} */
    @Override
    public void initializePopulation() {
        // Logging and notifying
        logger.info("executing initializePopulation function");
        notifySearchStarted();
        currentIteration = 0;

        // initialize population with random individuals
        this.generateInitialPopulation(Properties.POPULATION);

        // Evaluate for each fitness function

        // set grid resolution of the approximate archive
        // already done

        // foreach individual in Population, do
        List<T> tmpPop = new ArrayList<>(Properties.POPULATION);
        tmpPop.addAll(population);
        
        // Insert offspring floor(p) in the approximative archive Aeg such that only
        // non-dominated solutions remain
        // TODO: Create test to see if duplicates are put in approxArchive
        floorAndInsert(tmpPop);

        // Notify
        this.notifyIteration();
    }

    // Union function
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
