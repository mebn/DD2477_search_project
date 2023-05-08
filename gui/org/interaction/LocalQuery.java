package org.interaction;

public class LocalQuery {
    private String query;
    private int n;
    private boolean useSynonyms;

    public LocalQuery(String query, int n, boolean useSynonyms){
        this.query = query;
        this.n = n;
        this.useSynonyms = useSynonyms;
    }

    public String getQuery(){
        return this.query;
    }

    public int getN(){
        return this.n;
    }

    public boolean useSynonyms() {
        return this.useSynonyms;
    }
}
