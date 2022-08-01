package org.evosuite.ga.metaheuristics.sibea;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.DominanceComparator;
import org.evosuite.ga.operators.ranking.FastNonDominatedSorting;
import org.evosuite.ga.operators.ranking.RankingFunction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class HyperVolume<T extends Chromosome<T>> {
    // TODO: There is a package org.evosuite.ga.problems.metrics, though it's a cross-package??? Any way to use?

    private T reference;
    private Set<FitnessFunction<T>> objectives;

    protected RankingFunction<T> rankingFunction = new FastNonDominatedSorting<>();


    public HyperVolume(Set<? extends FitnessFunction<T>> goals) {
        this.objectives = new LinkedHashSet<>(goals);
    }

    public T getReference() {
        return reference;
    }

    public void setReference(T reference) {
        this.reference = reference;
    }

    public double computeHV(List<T> pop) {
        double a = 0.0;
        if (reference == null) {
            return 0.0;
        }
        return HV(pop);
    }

    /**
     * Compute the HyperVolume indicator.
     * WFG implementation.
     * @ARTICLE{5766730,
     *   author={While, Lyndon and Bradstreet, Lucas and Barone, Luigi},
     *   journal={IEEE Transactions on Evolutionary Computation},
     *   title={A Fast Way of Calculating Exact Hypervolumes},
     *   year={2012},
     *   volume={16},
     *   number={1},
     *   pages={86-95},
     *   doi={10.1109/TEVC.2010.2077298}}
     *
     * @param pop Population to measure HV indicator for.
     * @return HV value.
     */
    protected double HV(List<T> pop) {
        // return sum {exclHV(p1, k) | k in {1 .. |p1|}}
        pop.sort(new DominanceComparator<>());
        double sum = 0.0;
        for (int i = 0; i < pop.size(); i++) {
            sum += exclHV(pop, i);
        }
        return sum;
    }

    private double exclHV(List<T> pop, int k) {
        // return inclHV(p1[k]) - hypervolume(nds(limitSet(p1, k)))
        // Larger populations are broken down into smaller
        double volume = inclHV(pop.get(k));
        if (pop.size() > k + 1) {
            List<T> a = limitSet(pop, k);
            rankingFunction.computeRankingAssignment(a, objectives);
            volume -= HV(rankingFunction.getSubfront(0));
        }
        return volume;
    }

    private double inclHV(T p) {
        // return product {|p[j] - refPoint[j]| | j in {1 .. n}}
        double product = 1.0;
        for (FitnessFunction<T> ff : objectives) {
            product *= p.getFitness(ff) - reference.getFitness(ff);
        }
        return product;
    }

    private List<T> limitSet(List<T> pop, int k) {
        // for i = 1 to |p1| - k                        (for i = 0 to |pk| - 1 - k)
        //   for j = 1 to n                             (for j = 0 to objectives)
        //     ql[i][j] = worse(p1[k][j], p1[k+i][j])   (ql[i][j] = worse(pl[k][j], pl[k+1+i][j])
        // return ql                                    (return ql)
        List<T> q = new ArrayList<>(pop.size() - k);
        for (int i = 0; i < pop.size() - 1 - k; i++) {
            T tmp = pop.get(k).clone();
            for (FitnessFunction<T> ff : getObjectives()) {
                tmp.setFitness(ff,  worse(pop.get(k), pop.get(k+1+i), ff));
            }
            q.add(tmp);
        }
        return q;
    }
    private double worse(T a, T b, FitnessFunction<T> f) {
        return Math.max(a.getFitness(f), b.getFitness(f));
    }

    /**
     * Sort the population according to loss of HV metric if the individual were to be removed from the population.
     * @param population Population to sort for.
     */
    /** Sorts the population based on the HyperVolume indicator difference when the individual is removed. */
    public void HVSort(List<T> population) {
        double hvTotal = computeHV(population);
        population.sort((T t1, T t2) -> {
            List<T> tmp1 = new ArrayList<>(population);
            List<T> tmp2 = new ArrayList<>(population);
            tmp1.remove(t1);
            tmp2.remove(t2);
            double res1 = hvTotal - HV(tmp1);
            double res2 = hvTotal - HV(tmp2);
            if (res1 > res2)
                return 1;
            else if (res1 == res2)
                return 0;
            return -1;
        });
    }

    public Set<FitnessFunction<T>> getObjectives() {
        return objectives;
    }

    public void setObjectives(Set<FitnessFunction<T>> objectives) {
        this.objectives = objectives;
    }
}
