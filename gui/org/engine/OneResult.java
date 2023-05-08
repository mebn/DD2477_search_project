package org.engine;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.Map;

public class OneResult implements Comparable<OneResult>{
    String docId;
    String docName;
    float startTime;
    float endTime;

    String transcript;

    double score;

    public OneResult(String docId, String docName,float startTime,float endTime,String transcript,double score){
        this.docId = docId;
        this.docName = docName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transcript = transcript;
        this.score = score;
    }

    public String getTranscript() {
        return transcript;
    }

    @Override
    public int compareTo(OneResult o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return docName + " " + timeFormat(startTime) + "-" + timeFormat(endTime);
    }

    private String timeFormat(float time)
    {
        String timeStr;
        int minutes = (int)(time / 60.0);
        int seconds = (int)(time % 60.0);

        if(minutes < 10) {
            timeStr = "0" + minutes + ":";
        }

        else
            timeStr = minutes + ":";

        if(seconds < 10)
            timeStr += "0" + seconds;
        else
            timeStr += Integer.toString(seconds);

        return timeStr;
    }
}
