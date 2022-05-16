package org.evosuite.ga.operators.crossover;

import java.util.Arrays;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.DummyChromosome;
import org.junit.Test;
import org.junit.Assert;

public class MiddleCrossOverTest {
    @Test
    public void testSinglePointCrossOver() throws ConstructionFailedException {
        // create two parents with dummy chromosomes
        DummyChromosome parent1 = new DummyChromosome(1, 2, 3, 4);
        DummyChromosome parent2 = new DummyChromosome(5, 6);

        // pick a crossOver type for out dummy chromosomes
        MiddleCrossOver<DummyChromosome> xover = new MiddleCrossOver<DummyChromosome>();

        // create two dummy chromosome offsprings
        DummyChromosome offspring1 = new DummyChromosome(parent1);
        DummyChromosome offspring2 = new DummyChromosome(parent2);

        // crossOver the two offsprings
        xover.crossOver(offspring1, offspring2);

        // check that crossOver was done correctly for out offspring
        Assert.assertEquals(Arrays.asList(1, 2, 6), offspring1.getGenes());
        Assert.assertEquals(Arrays.asList(5, 3, 4), offspring2.getGenes());
    }
}
