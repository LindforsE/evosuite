package org.evosuite.ga.metaheuristics.sibea;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.DominanceComparator;
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

    private boolean permanentReference = false;
    private T referenceGoal;

    private final HyperVolume<T> HV;


    /**
     * Constructor
     *
     * @param factory a {@link ChromosomeFactory} object.
     */
    public SIBEA(ChromosomeFactory<T> factory) {
        super(factory);
        //rankingFunction = new FastNonDominatedSorting<>();
        HV = new HyperVolume<T>(new LinkedHashSet<FitnessFunction<T>>(fitnessFunctions));
    }

    /** {@inheritDoc} */
    @Override
    protected void evolve() {
        // (environmental selection) Iterate the following three steps until the
        // size of the population does no longer exceed mu
        List<T> = null;
        if (population.size() > Properties.POPULATION) {
            // 1. Rank the population using Pareto Dominance and determine
            // the set of individuals with the worst rank (P').
            int tmpPopulation = Properties.POPULATION;
            Properties.POPULATION = population.size();
            rankingFunction.computeRankingAssignment(population, new LinkedHashSet<FitnessFunction<T>>(fitnessFunctions));
            int fronts = rankingFunction.getNumberOfSubfronts();
            Properties.POPULATION = tmpPopulation;
            worst = rankingFunction.getSubfront(--fronts);

            // while difference is larger than size of worst front, remove whole front and get a new one.
            // 2023: Could this become an infinite loop?
                population.removeAll(worst);
                worst = rankingFunction.getSubfront(--fronts);
            }

            // 2. For each solution x from the worst rank P', determine
            // the loss d(x) with regard to the indicator I if it is removed from P'
            // i.e. d(x) := I(P') - I(P'\{x})
            if (!permanentReference)
                // 2023: What happens to the individual we assign as reference point?
                HV.setReference(lower(bestOf(worst)));
            else
                HV.setReference(referenceGoal);
            worst = HV.fasterHVSort(worst);
            //HV.HVSort(worst);

            // 3. Remove the solution with the smallest loss d(x) from
            // the population P (ties are broken randomly).
            int difference = population.size() - Properties.POPULATION;
            if (difference >= 0) {
                for (int i = 0; i < difference; i++)
                    population.remove(worst.get(i));
            }
        
        this.currentIteration++;
    }

    /**
     * Code for the mating step goes here.
     */
    public void mate() {
        // Randomly select elements from P to form a temporary mating pool Q of size mu.
        ArrayList<T> matingPop = new ArrayList<>(this.population.size());
        // 2023: Replaced "Properties.POPULATION" with "population.size()"
        for (int i = 0; i < population.size() / 2; i++) {
            T p1 = Randomness.choice(population);
            T p2 = Randomness.choice(population);

            T q1 = p1.clone();
            T q2 = p2.clone();

            // Apply variation operators such as recombination and mutation to mating pool Q to yield Q'.
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
        population.addAll(matingPop);
    }

    /** {@inheritDoc} */
    @Override
    public void initializePopulation() {
        logger.info("executing initializePopulation function");
        notifySearchStarted();

        // initialize population with random individuals
        this.generateInitialPopulation(Properties.POPULATION);

        for (T indiv : population) {
            for (FitnessFunction<T> ff : fitnessFunctions) {
                ff.getFitness(indiv);
            }
        }

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

    private T bestOf(List<T> pop) {
        List<T> tmp = new ArrayList<>(pop);
        tmp.sort(new DominanceComparator<>());
        return tmp.get(0);
    }

    private T lower(T individual) {
        for (FitnessFunction<T> ff : fitnessFunctions) {
            if (individual.getFitness(ff) -0.1 <= 0)
                individual.setFitness(ff, 0);
            else
                individual.setFitness(ff, individual.getFitness(ff) -0.1);
        }
        return individual;
    }

    public void setPermanentReference(boolean value, T reference) {
        this.permanentReference = value;
        this.referenceGoal = reference;
    }
}
