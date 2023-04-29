package org.interaction;

public class Query {
    private String query;
    private int n;

    public Query(String query, int n){
        this.query = query;
        this.n = n;
    }

    public String getQuery(){
        return this.query;
    }

    public int getN(){
        return this.n;
    }
}
