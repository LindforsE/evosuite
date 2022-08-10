package org.evosuite.ga.metaheuristics;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.runtime.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * MOEA/D implementation, Chebycheff approach, ILD weight vectors.
 * <INSERT ARTICLE HERE>
 * 
 * @author Elias
 */
public class MOEAD<T extends Chromosome<T>> extends GeneticAlgorithm<T> {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(MOEAD.class);

    // external population (EP), stores non-dominated solutions found during the search
    private final List<T> ep;

    // weight vectors (points)
    protected Double[][] weights;

    // reference vector is origo, aka (0,0,0,...,0)
    // does not need a variable

    // z, best individual for each sub-problem
    private final LinkedHashMap<FitnessFunction<T>, T> z;


    public MOEAD(ChromosomeFactory<T> factory) {
        super(factory);
        ep = new ArrayList<>();
        z = new LinkedHashMap<>();
        weights = new Double[Properties.POPULATION][fitnessFunctions.size()];
    }

    @Override
    protected void evolve() {
        // pop = population
        // N = Number of sub problems (objectives?)
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
            if (Random.nextDouble() <= Properties.MUTATION_RATE) {
                notifyMutation(offspring1);
                offspring1.mutate();
                notifyMutation(offspring2);
                offspring2.mutate();
            }

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
        // done

        for (FitnessFunction<T> f : fitnessFunctions) {
            z.put(f, null);
        }

        // distribute weight vectors (points)
        this.weights = distributeWeightVectors();


        // step 1.2
        // Compute the Euclidean distances between any
        // two weight vectors and then work out the closest weight
        // vectors to each weight vector

        // step 1.3
        // Generate an initial population
        // randomly or by a problem-specific method.
        this.generateInitialPopulation(Properties.POPULATION);

        // step 1.4
        // Initialize by a problem-specific method.
    }

    /**
     * Incremental lattice design (ILD), as specified in
     * @inproceedings{10.1145/3377929.3398082,
     * author = {Takagi, Tomoaki and Takadama, Keiki and Sato, Hiroyuki},
     * title = {Incremental Lattice Design of Weight Vector Set},
     * year = {2020},
     * isbn = {9781450371278},
     * publisher = {Association for Computing Machinery},
     * address = {New York, NY, USA},
     * url = {https://doi-org.miman.bib.bth.se/10.1145/3377929.3398082},
     * doi = {10.1145/3377929.3398082},
     * booktitle = {Proceedings of the 2020 Genetic and Evolutionary Computation Conference Companion},
     * pages = {1486–1494},
     * numpages = {9},
     * keywords = {weight vector set, many-objective optimization, multi-objective optimization, uniform mixture design, evolutionary algorithm},
     * location = {Canc\'{u}n, Mexico},
     * series = {GECCO '20}
     * }
     */
    protected Double[][] distributeWeightVectors() {
        Set<Double[]> L = new TreeSet<>(MOEAD::compare);
        List<Double[]> Lprim = new ArrayList<>();
        List<Double[]> edge = new ArrayList<>();

        // create origin point and add to L and edge
        Double[] originPoint = new Double[fitnessFunctions.size()];
        Arrays.fill(originPoint, 0.0);
        edge.add(originPoint);
        L.add(originPoint);

        int H = 0;
        // until we have one "vector" for each individual in population
        while (L.size() < Properties.POPULATION) {
            Lprim.clear();
            Lprim.addAll(L);
            L.clear();

            // create new double from edge and increase specific "fitness" by fitness-size, and put into L
            for (Double[] a : edge) {
                for (int i = 0; i < fitnessFunctions.size(); i++) {
                    // create new points for fitness'
                    Double[] tmp = a.clone();
                    tmp[i] += fitnessFunctions.size();
                    L.add(tmp);
                }
            }
            // get edges from L (all elements with a zero in it)
            edge.clear();
            int currentEdge = fitnessFunctions.size() -1;
            for (Double[] a : L) {
                for (Double aDouble : a) {
                    if (aDouble == 0) {
                        edge.add(a);
                        break;
                    }
                }
            }
            // increase all L' by 1, and put into L
            for (Double[] doubles : Lprim) {
                for (int j = 0; j < fitnessFunctions.size(); j++) {
                    doubles[j] += 1;
                }
                L.add(doubles);
            }
            H++;
        }

        // normalization
        for (Double[] doubles : L) {
            for (int j = 0; j < fitnessFunctions.size(); j++) {
                doubles[j] = doubles[j] / (fitnessFunctions.size() * H);
            }

        }
        return L.toArray(new Double[0][]);
    }

    private static int compare(Double[] doubles, Double[] t1) {
        if (doubles.length == t1.length) {
            for (int i = 0; i < doubles.length; i++) {
                if (doubles[i] > t1[i])
                    return 1;
                else if (doubles[i] < t1[i])
                    return -1;
            }
        } else {
            if (doubles.length > t1.length)
                return 1;
            return -1;
        }
        return 0;
    }
}
