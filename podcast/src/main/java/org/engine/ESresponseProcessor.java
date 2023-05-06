package org.engine;

import org.ES.ElasticSearchClient;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ml.dataframe.MlDataFrameAnalysisNamedXContentProvider;
import org.elasticsearch.core.Tuple;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.interaction.LocalQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ESresponseProcessor {
    int type = 0;

    ElasticSearchClient ESclient;
    public ESresponseProcessor(ElasticSearchClient ESclient,int type){
        this.type = type;
        this.ESclient = ESclient;
    }

    ArrayList<OneTranscriptSegment> groupFix(SearchResponse searchResponse, LocalQuery localQuery){
        ArrayList<OneTranscriptSegment> results = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for(SearchHit hit : searchHits) {
            String[] ids = hit.getId().split("_");
            String docId = ids[0];
            OneTranscriptSegment oneTranscriptSegment = processSegment(hit);
            results.add(oneTranscriptSegment);
        }
        return results;
    }

    public ArrayList<OneResult> group(SearchResponse searchResponse, LocalQuery localQuery){
        ArrayList<OneResult> results = null;
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap = new HashMap<>();
        for(SearchHit hit : searchHits) {
            String[] ids = hit.getId().split("_");
            String docId = ids[0];
            OneTranscriptSegment oneTranscriptSegment = processSegment(hit);
            if (!segmentsMap.containsKey(docId)){
                segmentsMap.put(docId,new ArrayList<>());
                segmentsMap.get(docId).add(oneTranscriptSegment);
            }
            else{
                segmentsMap.get(docId).add(oneTranscriptSegment);
            }
        }
        if (this.type==0){
            results = groupSegmentsWithDocid(segmentsMap,localQuery);
        }
        if (this.type==1){
            results = groupSegmentsWithFixMinutes(segmentsMap,localQuery);
        }
        return results;
    }

    public OneTranscriptSegment processSegment(SearchHit hit){
        double score = hit.getScore();
        String[] ids = hit.getId().split("_");
        String docId = ids[0];
        String segId = ids[1];
        Map<String,Object> sourceMap = hit.getSourceAsMap();
        String transcript = (String) sourceMap.get("transcript");
        ArrayList<HashMap> words = (ArrayList<HashMap>) sourceMap.get("words");
        String startTimeStr = words.get(0).get("startTime").toString();
        String endTimeStr = words.get(words.size()-1).get("endTime").toString();
        float startTime = Float.parseFloat(startTimeStr.substring(0,startTimeStr.length()-1));
        float endTime = Float.parseFloat(endTimeStr.substring(0,endTimeStr.length()-1));
        return new OneTranscriptSegment(docId,segId,startTime,endTime,transcript,score);
    }

    public ArrayList<OneResult> groupSegmentsWithDocid(HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap,LocalQuery query){
        ArrayList<OneResult> results = new ArrayList<>();
        int seconds = query.getN()*60;
        for(String docId:segmentsMap.keySet()){
            String docName = ESclient.getDocName(docId);
            ArrayList<OneTranscriptSegment> oneTranscriptSegments = segmentsMap.get(docId);
            float minStart = Float.MAX_VALUE;
            float maxEnd = Float.MIN_VALUE;
            StringBuilder transcripts = new StringBuilder();
            double score = 0;
            for(OneTranscriptSegment oneTranscriptSegment:oneTranscriptSegments){
                if (oneTranscriptSegment.startTime<minStart){
                    minStart = oneTranscriptSegment.startTime;
                }
                if (oneTranscriptSegment.endTime>maxEnd){
                    maxEnd = oneTranscriptSegment.endTime;
                }
                transcripts.append(oneTranscriptSegment.transcript);
                score += oneTranscriptSegment.score;
            }
            results.add(new OneResult(docId,docName,minStart,maxEnd,transcripts.toString(),score));
        }
        return results;
    }

    public ArrayList<OneResult> groupSegmentsWithFixMinutes(HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap,LocalQuery query){
        ArrayList<OneResult> results = new ArrayList<>();
        int seconds = query.getN()*60;
        for(String docId:segmentsMap.keySet()){
            String docName = ESclient.getDocName(docId);
            ArrayList<OneTranscriptSegment> oneTranscriptSegments = segmentsMap.get(docId);
            StringBuilder transcripts = new StringBuilder();
            double score = 0;
            HashMap<Tuple<Integer,Integer>,ArrayList<OneTranscriptSegment>> boundMap = new HashMap<>();
            for(OneTranscriptSegment oneTranscriptSegment:oneTranscriptSegments){
                int leftBound = (int) oneTranscriptSegment.startTime/60;
                int rightBound = leftBound+seconds;
                Tuple<Integer,Integer> bound = new Tuple<Integer,Integer>(leftBound,rightBound);
                if (!boundMap.containsKey(bound)){
                    boundMap.put(bound,new ArrayList<>());
                    boundMap.get(bound).add(oneTranscriptSegment);
                }
                else{
                    boundMap.get(bound).add(oneTranscriptSegment);
                }
            }
            for(Tuple<Integer,Integer> bound:boundMap.keySet()){
                ArrayList<OneTranscriptSegment> oneTranscriptSegmentsList = boundMap.get(bound);
                for(OneTranscriptSegment oneTranscriptSegment:oneTranscriptSegmentsList){
                    transcripts.append(oneTranscriptSegment.transcript);
                    score += oneTranscriptSegment.score;
                }
                results.add(new OneResult(docId,docName,bound.v1(),bound.v2(),transcripts.toString(),score));
            }
        }
        return results;
    }
}
