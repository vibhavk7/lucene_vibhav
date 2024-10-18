package ie.tcd.vib;

class sResult {
    private static String Q0 = "Q0";

    private int qId, docId, rank;
    private float score;
    private String runId;

    public sResult(int query, int doc, int rank, float score, String runId) {
        this.qId = query;
        this.docId = doc;
        this.rank = rank;
        this.score = score;
        this.runId = runId;
    }

    public String getRunId() {
        return this.runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    public int getqId() {
        return this.qId;
    }

    public void setqId(int id) {
        this.qId = id;
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
        return "qId: " + Integer.toString(qId) + "\n"
            + "Q0: " + Q0 + "\n"
            + "DocID: " + Integer.toString(docId) + "\n"
            + "Rank: " + Integer.toString(rank) + "\n"
            + "Score: " + Float.toString(score) + "\n"
            + "RunId: " + getRunId();
    }

    public String tTrecFormevl() {
        return Integer.toString(qId) + "\t"
            + Q0 + "\t"
            + Integer.toString(docId) + "\t"
            + Integer.toString(rank) + "\t"
            + Float.toString(score) + "\t"
            + getRunId();
    }
}