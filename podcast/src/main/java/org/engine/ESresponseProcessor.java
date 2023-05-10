package org.engine;

import org.ES.ElasticSearchClient;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ml.dataframe.MlDataFrameAnalysisNamedXContentProvider;
import org.elasticsearch.core.Tuple;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.interaction.LocalQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ESresponseProcessor {
    int type = 0;

    ElasticSearchClient ESclient;
    public ESresponseProcessor(ElasticSearchClient ESclient,int type){
        this.type = type;
        this.ESclient = ESclient;
    }

    public ArrayList<OneResult> group(LocalQuery localQuery){
        SearchResponse searchResponse;
        if(localQuery.getN()==1){
            searchResponse = ESclient.search("episodes",localQuery);
        }
        else{
            searchResponse = ESclient.search("episodes_2min",localQuery);
        }
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
        if (this.type==2){
            results = groupSegmentsWithNminutes(segmentsMap,localQuery);
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

    public Tuple<Float, Float> getSegmentTimes(GetResponse getResponse){
        Map<String,Object> sourceMap = getResponse.getSourceAsMap();
        String transcript = (String) sourceMap.get("transcript");
        ArrayList<HashMap> words = (ArrayList<HashMap>) sourceMap.get("words");
        String startTimeStr = words.get(0).get("startTime").toString();
        String endTimeStr = words.get(words.size()-1).get("endTime").toString();
        float startTime = Float.parseFloat(startTimeStr.substring(0,startTimeStr.length()-1));
        float endTime = Float.parseFloat(endTimeStr.substring(0,endTimeStr.length()-1));
        return new Tuple(startTime,endTime);
    }


    public OneTranscriptSegment getSmallerSegment(GetResponse getResponse,int seconds, String part){
        String[] ids = getResponse.getId().split("_");
        String docId = ids[0];
        String segId = ids[1];
        Map<String,Object> sourceMap = getResponse.getSourceAsMap();
        ArrayList<HashMap> words = (ArrayList<HashMap>) sourceMap.get("words");
        String startTimeStr = words.get(0).get("startTime").toString();
        String endTimeStr = words.get(words.size()-1).get("endTime").toString();
        float startTime = Float.parseFloat(startTimeStr.substring(0,startTimeStr.length()-1));
        float endTime = Float.parseFloat(endTimeStr.substring(0,endTimeStr.length()-1));
        StringBuilder transcript = new StringBuilder();
//        int leftIndex = 0;
//        int rightIndex = words.size()-1;
        int midIndex = 0;
        float midTime = 0;
        Tuple<Float, Float> segTimes = getSegmentTimes(getResponse);
        double segLength = Math.floor(segTimes.v2() - segTimes.v1());
        if(Objects.equals(part, "start")){
            midIndex = (int) (0+Math.floor(seconds/segLength*words.size()-1));
            String midTimeString = words.get(midIndex).get("endTime").toString();
            midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
            if(Math.abs((midTime-startTime)-seconds)>1){
                if((midTime-startTime)-seconds<0){
                    while(midIndex<words.size()-1) {
                        midIndex += 1;
                        midTimeString = words.get(midIndex).get("endTime").toString();
                        midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
                        if ((midTime-startTime)-seconds>0) {
                            break;
                        }
                    }
                }
                else{
                    while(midIndex>0){
                        midIndex -= 1;
                        midTimeString = words.get(midIndex).get("endTime").toString();
                        midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
                        if ((midTime - startTime) - seconds<0) {
                            break;
                        }
                    }
                }
            }
            for(int i =0;i<=midIndex;i++){
                transcript.append(words.get(i).get("word")).append(" ");
            }
            return new OneTranscriptSegment(docId,segId,startTime,midTime,transcript.toString(),0.0);
        }
        if(Objects.equals(part, "end")){
            midIndex = (int) (0+Math.floor(seconds/segLength*words.size()-1));
            String midTimeString = words.get(midIndex).get("endTime").toString();
            midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
            if(Math.abs((endTime-midTime)-seconds)>1){
                if((endTime-midTime)-seconds>0){
                    while(midIndex<words.size()-1) {
                        midIndex += 1;
                        midTimeString = words.get(midIndex).get("endTime").toString();
                        midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
                        if ((endTime-midTime) - seconds < 0) {
                            break;
                        }
                    }
                }
                else{
                    while(midIndex>0){
                        midIndex -= 1;
                        midTimeString = words.get(midIndex).get("endTime").toString();
                        midTime = Float.parseFloat(midTimeString.substring(0,midTimeString.length()-1));
                        if ((endTime-midTime) - seconds > 0) {
                            break;
                        }
                    }
                }
            }
            for(int i =midIndex;i<words.size();i++){
                transcript.append(words.get(i).get("word")).append(" ");
            }
            return new OneTranscriptSegment(docId,segId,midTime,endTime,transcript.toString(),0.0);
        }
        return null;
    }

    public OneResult expandSegment(String index,OneTranscriptSegment oneTranscriptSegment,int seconds,String docId,String docName,String episodeName){
        StringBuilder transcripts = new StringBuilder();
        int nowSegid = Integer.parseInt(oneTranscriptSegment.segId);
        float nowStartTime = oneTranscriptSegment.startTime;
        float nowEndTime = oneTranscriptSegment.endTime;
        double nowScore = oneTranscriptSegment.score;
        double nowLength = nowEndTime-nowStartTime;
        float lastStartTime = nowStartTime;
        float nextEndTime = nowEndTime;
        int needSeconds = (int) Math.floor(seconds - nowLength);
        int halfNeedSeconds = 0;
        int lastSegid = 0;
        int nextSegid = 0;
        if(Objects.equals(index, "episodes")){
            lastSegid = nowSegid - 1;
            nextSegid = nowSegid + 1;
        }
        else{
            lastSegid = nowSegid - 2;
            nextSegid = nowSegid + 2;
        }
        if(lastSegid>=0) {
            halfNeedSeconds = needSeconds / 2;
            if (lastStartTime-halfNeedSeconds>=0){
                lastStartTime -= halfNeedSeconds;
                nextEndTime += halfNeedSeconds;
            }
            else{
                lastStartTime = 0;
                nextEndTime += halfNeedSeconds+(halfNeedSeconds-lastStartTime);
            }
            while ((halfNeedSeconds > 0)&&(lastSegid>0)) {
                GetResponse lastResponse = ESclient.getTranscript(index, oneTranscriptSegment.docId, lastSegid);
                Tuple<Float, Float> segTimes = getSegmentTimes(lastResponse);
                double segLength = Math.ceil(segTimes.v2() - segTimes.v1());
                if (lastResponse != null) {
                    if (halfNeedSeconds < segLength) {
                        OneTranscriptSegment smallerSegment = getSmallerSegment(lastResponse, halfNeedSeconds, "end");
                        transcripts.append(smallerSegment.transcript);
                    } else {
                        transcripts.append(lastResponse.getSourceAsMap().get("transcript"));
                    }
                    halfNeedSeconds -= segLength;
                } else {
                    break;
                }
                if(Objects.equals(index, "episodes")){
                    lastSegid = nowSegid - 1;
                }
                else{
                    lastSegid = nowSegid - 2;
                }
            }
            transcripts.append(oneTranscriptSegment.transcript);
            halfNeedSeconds = needSeconds / 2;
            while (halfNeedSeconds > 0) {
                GetResponse nextResponse = ESclient.getTranscript(index,oneTranscriptSegment.docId, nextSegid);
                Tuple<Float, Float> segTimes = getSegmentTimes(nextResponse);
                double segLength = Math.ceil(segTimes.v2() - segTimes.v1());
                if (nextResponse != null) {
                    if (halfNeedSeconds < segLength) {
                        OneTranscriptSegment smallerSegment = getSmallerSegment(nextResponse, halfNeedSeconds, "start");
                        transcripts.append(smallerSegment.transcript);
                    } else {
                        transcripts.append(nextResponse.getSourceAsMap().get("transcript"));
                    }
                    halfNeedSeconds -= segLength;
                } else {
                    break;
                }
                if(Objects.equals(index, "episodes")){
                    nextSegid = nowSegid + 1;
                }
                else{
                    nextSegid = nowSegid + 2;
                }
            }
        }
        else{
            halfNeedSeconds = needSeconds;
            nextEndTime += halfNeedSeconds;
            transcripts.append(oneTranscriptSegment.transcript);
            while (halfNeedSeconds > 0) {
                GetResponse nextResponse = ESclient.getTranscript(index, oneTranscriptSegment.docId, nextSegid);
                Tuple<Float, Float> segTimes = getSegmentTimes(nextResponse);
                double segLength = Math.ceil(segTimes.v2() - segTimes.v1());
                if (nextResponse != null) {
                    if (halfNeedSeconds < segLength) {
                        OneTranscriptSegment smallerSegment = getSmallerSegment(nextResponse, halfNeedSeconds, "start");
                        transcripts.append(smallerSegment.transcript);
                    } else {
                        transcripts.append(nextResponse.getSourceAsMap().get("transcript"));
                    }
                    halfNeedSeconds -= segLength;
                } else {
                    break;
                }
                if (Objects.equals(index, "episodes")) {
                    nextSegid = nowSegid + 1;
                } else {
                    nextSegid = nowSegid + 2;
                }
            }
        }
        return new OneResult(docId, docName,episodeName,lastStartTime, nextEndTime, transcripts.toString(), nowScore);
    }

    public ArrayList<OneResult> groupSegmentsWithNminutes(HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap,LocalQuery query){
        ArrayList<OneResult> results = new ArrayList<>();
        int seconds = query.getN()*60;
        for(String docId:segmentsMap.keySet()) {
            Tuple<String,String> names = ESclient.getDocNames(docId);
            String docName = names.v1();
            String episodeName = names.v2();
            ArrayList<OneTranscriptSegment> oneTranscriptSegments = segmentsMap.get(docId);
            StringBuilder transcripts = new StringBuilder();
            for (OneTranscriptSegment oneTranscriptSegment : oneTranscriptSegments) {
                int nowSegid = Integer.parseInt(oneTranscriptSegment.segId);
                float nowStartTime = oneTranscriptSegment.startTime;
                float nowEndTime = oneTranscriptSegment.endTime;
                double nowScore = oneTranscriptSegment.score;
                float lastStartTime = nowStartTime;
                float nextEndTime = nowEndTime;
                transcripts.append(oneTranscriptSegment.transcript);
                if(seconds==120){
                    results.add(new OneResult(docId, docName, episodeName,lastStartTime, nextEndTime, transcripts.toString(), nowScore));
                }
                else if(seconds<120){
                    results.add(expandSegment("episodes",oneTranscriptSegment,seconds,docId, docName,episodeName));
                }
                else {
                    results.add(expandSegment("episodes_2min",oneTranscriptSegment,seconds,docId, docName,episodeName));
                }
            }
        }
        return results;
    }

    public ArrayList<OneResult> groupSegmentsWithDocid(HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap,LocalQuery query){
        ArrayList<OneResult> results = new ArrayList<>();
        int seconds = query.getN()*60;
        for(String docId:segmentsMap.keySet()){
            Tuple<String,String> names = ESclient.getDocNames(docId);
            String docName = names.v1();
            String episodeName = names.v2();
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
            results.add(new OneResult(docId,docName,episodeName,minStart,maxEnd,transcripts.toString(),score));
        }
        return results;
    }

    public ArrayList<OneResult> groupSegmentsWithFixMinutes(HashMap<String,ArrayList<OneTranscriptSegment>> segmentsMap,LocalQuery query){
        ArrayList<OneResult> results = new ArrayList<>();
        int seconds = query.getN()*60;
        for(String docId:segmentsMap.keySet()){
            Tuple<String,String> names = ESclient.getDocNames(docId);
            String docName = names.v1();
            String episodeName = names.v2();
            ArrayList<OneTranscriptSegment> oneTranscriptSegments = segmentsMap.get(docId);
            StringBuilder transcripts = new StringBuilder();
            double score = 0;
            HashMap<Tuple<Integer,Integer>,ArrayList<OneTranscriptSegment>> boundMap = new HashMap<>();
            for(OneTranscriptSegment oneTranscriptSegment:oneTranscriptSegments){
                int leftBound = (int) oneTranscriptSegment.startTime/60*60;
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
                results.add(new OneResult(docId,docName,episodeName,bound.v1(),bound.v2(),transcripts.toString(),score));
            }
        }
        return results;
    }
    // for evaluating
    ArrayList<OneTranscriptSegment> groupFix(SearchResponse searchResponse, LocalQuery localQuery){
        ArrayList<OneTranscriptSegment> results = new ArrayList<>();
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for(SearchHit hit : searchHits) {
            OneTranscriptSegment oneTranscriptSegment = processSegment(hit);
            results.add(oneTranscriptSegment);
        }
        return results;
    }
}
