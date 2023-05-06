package org.engine;

public class TestLabel implements Comparable<TestLabel>{
    String segId;
    double score;

    public TestLabel(String segId, double score){
        this.segId = segId;
        this.score = score;
    }

    @Override
    public int compareTo(TestLabel o) {
        return Double.compare(score, o.score);
    }
}
