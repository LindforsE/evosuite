package org.evosuite.ga.metaheuristics;

import org.evosuite.SystemTestBase;
import org.evosuite.ga.DummyChromosome;
import org.junit.Test;
import org.junit.Assert;

public class MOEADSystemTest extends SystemTestBase {

    @Test
    public void testEmptyPopulation() {
        MOEAD<DummyChromosome> ga = new MOEAD<>(null);

        Assert.assertTrue(true);
    }

    @Test
    public void testInitialPopulation() {
        MOEAD<DummyChromosome> ga = new MOEAD<>(null);
        ga.initializePopulation();

    }
}
