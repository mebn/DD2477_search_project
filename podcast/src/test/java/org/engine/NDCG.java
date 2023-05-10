package org.engine;

import java.util.*;
import java.util.stream.Collectors;

public class NDCG {
    public static double compute(List<String> rankedSegments,HashMap<String,Integer> correctSegments) {
        
        double dcg   = 0;
        double idcg  = computeIDCG(correctSegments);

        for (int i = 0; i < rankedSegments.size(); i++) {
            String itemId = rankedSegments.get(i);
            if (!correctSegments.containsKey(itemId))
                continue;
            // compute NDCG part
            int rank = i + 1;
            dcg += correctSegments.get(itemId) / Math.log(rank + 1);
        }

        return dcg / idcg;
    }
    
    static double computeIDCG(HashMap<String,Integer> correctSegments)
    {
        LinkedHashMap<String, Integer> sortedMap = correctSegments.entrySet().stream().
                sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        double idcg = 0;
        int i = 0;
        for (String key:sortedMap.keySet()) {
            idcg += sortedMap.get(key) / Math.log(i + 2);
            i += 1;
        }
        return idcg;
    }
}
