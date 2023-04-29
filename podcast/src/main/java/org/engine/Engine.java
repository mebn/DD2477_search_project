package org.engine;

import org.ES.ElasticSearchClient;
import org.interaction.LocalQuery;

public class App 
{
    public static void main(String[] args ){
        String host = "20.223.162.103";
        int port  = 9200;
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient(host,port);
        LocalQuery query = new LocalQuery("coronavirus spread",2);
        elasticSearchClient.search(query);
    }
}
