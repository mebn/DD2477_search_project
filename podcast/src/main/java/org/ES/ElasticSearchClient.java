package org.elasticsearch;


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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.interaction.LocalQuery;

import java.io.IOException;
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

    public void search(LocalQuery query){
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("episodes");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(query.getQuery()).field("transcript");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("Successful response");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args ){
        String host = "20.223.162.103";
        int port  = 9200;
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient(host,port);
        LocalQuery query = new LocalQuery("coronavirus spread",2);
        elasticSearchClient.search(query);
    }

}
