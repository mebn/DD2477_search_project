package org.engine;

public class OneTranscriptSegment {
    String docId;

    String segId;

    float startTime;
    float endTime;
    String transcript;

    double score;

    public OneTranscriptSegment(String docId,String segId,float startTime,float endTime,String transcript,double score){
        this.docId = docId;
        this.segId = segId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.transcript = transcript;
        this.score = score;
    }

}
