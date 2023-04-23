public class Result {
    private String filename;
    /*
    * Here we can have timestamps, podcast name and other search result data
    *
    * */
    public Result(String text) {
        this.filename = text;
    }

    @Override
    public String toString() {
        // This is used to display the search result in the scroll-pane
        return this.filename;
    }
}
