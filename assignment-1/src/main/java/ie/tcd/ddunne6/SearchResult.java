package ie.tcd.ddunne6;

class SearchResult {
    private static String STANDARD = "STANDARD";
    private static String Q0 = "Q0";

    private int queryId, docId, rank;
    private float score;

    public SearchResult(int query, int doc, int rank, float score) {
        this.queryId = query;
        this.docId = docId;
        this.rank = rank;
        this.score = score;
    }
    
    public int getQueryId() {
        return this.queryId;
    }

    public void setQueryId(int id) {
        this.queryId = id;
    }

    public int getDocId() {
        return this.docId;
    }

    public void setDocId(int id) {
        this.docId = id;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public float getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "QueryID: " + Integer.toString(queryId) + "\n"
            + "Q0: " + Q0 + "\n"
            + "DocID: " + Integer.toString(docId) + "\n"
            + "Rank: " + Integer.toString(rank) + "\n"
            + "Score: " + Float.toString(score) + "\n"
            + "Standard: " + STANDARD;
    }

    public String toTrecEvalFormat() {
        return Integer.toString(queryId) + "\t"
            + Q0 + "\t"
            + Integer.toString(docId) + "\t"
            + Integer.toString(rank) + "\t"
            + Float.toString(score) + "\t"
            + STANDARD;
    }
}