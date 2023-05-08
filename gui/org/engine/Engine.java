package org.engine;

import org.ES.ElasticSearchClient;
import org.elasticsearch.action.search.SearchResponse;
import org.interaction.LocalQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Engine {
    public static ElasticSearchClient client = null;

    public static void initSearcher(){
        String host = "20.223.162.103";
        int port  = 9200;
        int groupType = 0;
        client = new ElasticSearchClient(host,port);
        LocalQuery query = new LocalQuery("coronavirus spread",2, false);
        SearchResponse searchResponse = client.search(query);
        ESresponseProcessor eSresponseProcessor = new ESresponseProcessor(client,groupType);
        ArrayList<OneResult> results = eSresponseProcessor.group(searchResponse,query);
        Collections.sort(results,Collections.reverseOrder());
    }
}
