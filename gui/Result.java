import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Result {

    String _index;
    String _id;
    double _score;
    String[] _ignored;
    SourceObject _source;

    public class SourceObject {
        ResultObject[] results; // Will this always be one-long????

        public class ResultObject {
            Alternative[] alternatives;

            public class Alternative {
                String transcript;
                double confidence;
                WordHit[] words;
                public class WordHit {
                    String startTime;
                    String endTime;
                    String word;
                }
            }
        }

    }

    /*
    * Generated from response JSON representing each individual result
    *
    * */

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
