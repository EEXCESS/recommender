package eu.eexcess.dataformats;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RecommenderStatsTest {
    @Test
    public void recommenderStatsTestSimpleSet() {
        RecommenderStats recommenderStats = new RecommenderStats();
        long averageGlobalTime = 3000;
        recommenderStats.setAverageGlobalTime(averageGlobalTime);
        assertTrue(Long.compare(recommenderStats.getAverageGlobalTime(), averageGlobalTime) == 0);
    }

    @Test
    public void recommenderStatsTestSet2Times() {
        RecommenderStats recommenderStats = new RecommenderStats();
        long averageGlobalTime1 = 3000;
        long averageGlobalTime2 = 0;
        recommenderStats.setAverageGlobalTime(averageGlobalTime1);
        recommenderStats.setAverageGlobalTime(averageGlobalTime2);
        assertTrue(Long.compare(recommenderStats.getAverageGlobalTime(), ((averageGlobalTime1 + averageGlobalTime2) / 2)) == 0);

    }

    @Test
    public void recommenderStatsTestSet4Times() {
        RecommenderStats recommenderStats = new RecommenderStats();
        long averageGlobalTime1 = 3000;
        long averageGlobalTime2 = 0;
        long averageGlobalTime3 = 10000;
        long averageGlobalTime4 = 100;
        recommenderStats.setAverageGlobalTime(averageGlobalTime1);

        recommenderStats.setAverageGlobalTime(averageGlobalTime2);

        recommenderStats.setAverageGlobalTime(averageGlobalTime3);

        recommenderStats.setAverageGlobalTime(averageGlobalTime4);

        assertTrue(Long.compare(recommenderStats.getAverageGlobalTime(), ((((((averageGlobalTime1 + averageGlobalTime2) / 2) + averageGlobalTime3) / 2) + averageGlobalTime4) / 2)) == 0);

    }

    @Test
    public void recommenderStatsTestSet5Times() {
        RecommenderStats recommenderStats = new RecommenderStats();
        long averageGlobalTime1 = 3000;
        long averageGlobalTime2 = 0;
        long averageGlobalTime3 = 10000;
        long averageGlobalTime4 = 100;
        long averageGlobalTime5 = 5710;

        recommenderStats.setAverageGlobalTime(averageGlobalTime1);
        recommenderStats.setAverageGlobalTime(averageGlobalTime2);
        recommenderStats.setAverageGlobalTime(averageGlobalTime3);
        recommenderStats.setAverageGlobalTime(averageGlobalTime4);
        recommenderStats.setAverageGlobalTime(averageGlobalTime5);

        final long t1 = (averageGlobalTime1 + averageGlobalTime2) / 2;
        final long t2 = (t1 + averageGlobalTime3) / 2;
        final long t3 = (t2 + averageGlobalTime4) / 2;
        final long t4 = (t3 + averageGlobalTime5) / 2;

        assertTrue(Long.compare(recommenderStats.getAverageGlobalTime(), t4) == 0);
    }
}
