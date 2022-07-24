package org.evosuite.ga.metaheuristics;

import org.evosuite.SystemTestBase;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class SIBEASystemTest extends SystemTestBase {
    @BeforeClass
    public static void setUp() {

    }
    @Test
    public void testSIBEACall() {
        SIBEA<TestSuiteChromosome> ga = new SIBEA<>(null);
        assertTrue(true);
    }

}
