package org.interaction;

public class LocalQuery {
    private String query;
    private int n;
    private boolean useSynonym;

    public LocalQuery(String query, int n, boolean useSynonym){
        this.query = query;
        this.n = n;
        this.useSynonym = useSynonym;
    }

    public String getQuery(){
        return this.query;
    }

    public int getN(){
        return this.n;
    }

    public boolean useSynonym() { return this.useSynonym; }
}
