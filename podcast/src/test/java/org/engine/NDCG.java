package org.engine;

import java.util.HashSet;
import java.util.List;

public class NDCG {
    public static double compute(List<String> rankedSegments, List<String> correctSegments) {
        
        double dcg   = 0;
        double idcg  = computeIDCG(correctSegments.size());

        for (int i = 0; i < rankedSegments.size(); i++) {
            String itemId = rankedSegments.get(i);
            if (!correctSegments.contains(itemId))
                continue;
            // compute NDCG part
            int rank = i + 1;
            dcg += Math.log(2) / Math.log(rank + 1);
        }

        return dcg / idcg;
    }
    
    static double computeIDCG(int n)
    {
        double idcg = 0;
        for (int i = 0; i < n; i++)
            idcg += Math.log(2) / Math.log(i + 2);
        return idcg;
    }
}
