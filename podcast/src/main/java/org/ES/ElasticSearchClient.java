package org.ES;


import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.interaction.LocalQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ElasticSearchClient {

    private String host;
    private  int port;

    RestHighLevelClient restHighLevelClient;
    public ElasticSearchClient(String host,int port){
        this.host = host;
        this.port = port;
        RestClient httpClient = RestClient.builder(new HttpHost(host, port)).build();
        restHighLevelClient = new RestHighLevelClientBuilder(httpClient)
                .setApiCompatibilityMode(true)
                .build();
    }

    public void close() throws IOException {
        restHighLevelClient.close();
    }

    public SearchResponse search(String index,LocalQuery query){
        SearchResponse searchResponse = null;
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
//        searchRequest.indices("episodes_2min");
//        searchRequest.indices("episodes");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query.getQuery()).field("transcript");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//            System.out.println("Successful response");
        } catch (IOException e){
            e.printStackTrace();
        }
        return searchResponse;
    }

    public GetResponse getTranscript(String index,String docId,int segId) {
        GetResponse getResponse = null;
        String id = docId+"_"+ segId;
//        GetRequest getRequest = new GetRequest("episodes",id);
        GetRequest getRequest = new GetRequest(index,id);
        try{
            getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
        } catch (IOException e){
//            e.printStackTrace();
            return null;
        }
        return getResponse;
    }

    public String getDocName(String docId) {
        GetResponse getResponse = null;
        String docName = null;
        GetRequest getRequest = new GetRequest("metadata",docId);
        try{
            getResponse = restHighLevelClient.get(getRequest,RequestOptions.DEFAULT);
            docName  = getResponse.getSourceAsMap().get("show_name").toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return docName;
    }


}
