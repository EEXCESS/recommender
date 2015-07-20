/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.diversityasurement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.junit.Test;

public class CorrelationTest {

    private static class CorrelationValues {
        public double[] x;
        public double[] y;
        public double spearmanRho;
        public double kendallTaoB;
        public double epsilon = 0.01;
    }

    private CorrelationValues newCorrelationValue1() {
        CorrelationValues c = new CorrelationValues();
        c.x = new double[] { 56, 75, 45, 71, 61, 64, 58, 80, 76, 61 };
        c.y = new double[] { 66, 70, 40, 60, 65, 56, 59, 77, 67, 63 };
        c.spearmanRho = 0.67;
        c.kendallTaoB = 0.49;
        return c;
    }

    private CorrelationValues newCorrelationValue2() {
        CorrelationValues c = new CorrelationValues();
        c.x = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        c.y = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        c.spearmanRho = 1;
        c.kendallTaoB = 1;
        return c;
    }

    private CorrelationValues newCorrelationValue3() {
        CorrelationValues c = new CorrelationValues();
        c.x = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        c.y = new double[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        c.spearmanRho = -1;
        c.kendallTaoB = -1;
        return c;
    }

    private CorrelationValues newCorrelationValue4() {
        CorrelationValues c = new CorrelationValues();
        c.x = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        c.y = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        c.spearmanRho = 0;
        c.kendallTaoB = 1;
        return c;
    }

    @Test
    public void spearmansRho_givenDefaultValues_expectCorrectResult() {
        CorrelationValues c = newCorrelationValue1();
        SpearmansCorrelation sCorrelation = new SpearmansCorrelation();
        PearsonsCorrelation pCorrelatoin = new PearsonsCorrelation();

        System.out.println("spearman correlation r(x, y)=" + sCorrelation.correlation(c.x, c.y));
        assertEquals(sCorrelation.correlation(c.x, c.y), c.spearmanRho, c.epsilon);
        System.out.println("pearson correlation  r(x, y)=" + pCorrelatoin.correlation(c.x, c.y));
        assertEquals(sCorrelation.correlation(c.y, c.x), c.spearmanRho, c.epsilon);
    }

    @Test
    public void spearmansRho_givenAKartesianQuadrandIIandIVSymmetralePrallelLine() {
        CorrelationValues c = newCorrelationValue2();
        SpearmansCorrelation sCorrelation = new SpearmansCorrelation();
        System.out.println("spearman correlation r(x, y)=" + sCorrelation.correlation(c.x, c.y));
        assertEquals(sCorrelation.correlation(c.x, c.y), c.spearmanRho, c.epsilon);
    }

    @Test
    public void spearmansRho_givenAKartesianQuadrandIandIIISymmetraleParallelLine() {
        CorrelationValues c = newCorrelationValue3();
        SpearmansCorrelation sCorrelation = new SpearmansCorrelation();
        System.out.println("spearman correlation r(x, y)=" + sCorrelation.correlation(c.x, c.y));
        assertEquals(sCorrelation.correlation(c.x, c.y), c.spearmanRho, c.epsilon);
    }

    @Test
    public void spearmansRho_givenPosXAxis() {
        CorrelationValues c = newCorrelationValue4();
        SpearmansCorrelation sCorrelation = new SpearmansCorrelation();
        System.out.println("spearman correlation r(x, y)=" + sCorrelation.correlation(c.x, c.y));
        assertTrue(Double.isNaN(sCorrelation.correlation(c.x, c.y)));
    }

    @Test
    public void kendallsTao_givenAKartesianQuadrandIandIIISymmetraleParallelLine() {
        CorrelationValues c = newCorrelationValue3();
        KendallsCorrelation kCorrelation = new KendallsCorrelation();
        System.out.println("kendall's tao correlation t(x, y)=" + kCorrelation.correlation(c.x, c.y));
        assertEquals(c.kendallTaoB, kCorrelation.correlation(c.x, c.y), c.epsilon);
    }

    @Test
    public void kendallsTao_givenAKartesianQuadrandIIandIVSymmetralePrallelLine() {
        CorrelationValues c = newCorrelationValue2();
        KendallsCorrelation kCorrelation = new KendallsCorrelation();
        System.out.println("kendall's tao correlation t(x, y)=" + kCorrelation.correlation(c.x, c.y));
        assertEquals(c.kendallTaoB, kCorrelation.correlation(c.x, c.y), c.epsilon);
    }

    @Test
    public void kendallsTao_givenDefaultValues_expectCorrectResult() {
        CorrelationValues c = newCorrelationValue1();
        KendallsCorrelation kCorrelation = new KendallsCorrelation();
        System.out.println("kendall's tao correlation t(x, y)=" + kCorrelation.correlation(c.x, c.y));
        assertEquals(c.kendallTaoB, kCorrelation.correlation(c.x, c.y), c.epsilon);
    }
}
