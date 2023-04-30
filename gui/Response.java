public class Response {
    String took;
    String timed_out;
    //String _shards;
    HitsObject hits;

    public class HitsObject {
        TotalObject total;
        double max_score;
        Result[] hits;

        public class TotalObject {
            int value;
            String relation;
        }

    }

    public Result[] getHits() {
        return hits.hits;
    }
}
