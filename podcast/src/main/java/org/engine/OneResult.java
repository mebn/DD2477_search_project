package org.engine;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Map;

public class OneResult implements Comparable<OneResult>{
    String docId;
    String docName;
    String episodeName;
    float startTime;
    float endTime;

    String transcript;

    double score;

    public OneResult(String docId, String docName,String episodeName,float startTime,float endTime,String transcript,double score){
        this.docId = docId;
        this.docName = docName;
        this.episodeName = episodeName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transcript = transcript;
        this.score = score;
    }

    @Override
    public int compareTo(OneResult o) {
        return Double.compare(score, o.score);
    }

    public Object getTranscript() {
        return transcript;
    }
}
