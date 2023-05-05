package org.engine;

public class OneTranscriptSegment implements Comparable<OneTranscriptSegment>{
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

    @Override
    public int compareTo(OneTranscriptSegment o) {
        return Double.compare(score, o.score);
    }

}
