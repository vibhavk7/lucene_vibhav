package ie.tcd.ddunne6;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

class QueryIndex {
    // the location of the search index
	private static String INDEX_DIRECTORY = "../index";
    // Limit the number of search results we get
	private static int MAX_RESULTS = 50;
    private static String RESULTS_DIRECTORY = "results/";

    private String scoringApproach;

    private ArrayList<CranQuery> queries = new ArrayList<CranQuery>();
    private ArrayList<SearchResult> results = new ArrayList<SearchResult>();

    public QueryIndex(String path, Analyzer analyzer, Similarity similarity, String type) {
        try {
            parseCorpus(path);
            
            setScoringApproach(type);
            queryIndexFromQueries(analyzer, similarity);
            saveToFile();
        } catch (Exception e) {
            System.out.println("Issue with QueryIndex.");
            e.printStackTrace();
        }
    }

    public void parseCorpus(String path) throws IOException {
        // File opening Setup
        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine;

        int queryId = 1;
        String queryBody = "";
        Boolean firstIteration = true;

        System.out.println("<--- STARTED Parsing " + path +" --->");

        //Read File Line By Line
        while ((strLine = br.readLine()) != null) {
            if(isIdTag(strLine)) {
                // add ID and content to Query
                if(!firstIteration) {
                    CranQuery query = new CranQuery(queryId, queryBody);
                    addQuery(query);
                    // Clear buffer, Iterate ID
                    queryId++;
                    queryBody = "";
                }
                firstIteration = false;
            }
            else if(isNotBodyTag(strLine)) {
                if(queryBody == "") {
                    queryBody = strLine;
                }
                else {
                    queryBody = queryBody + "\n" + strLine;
                }
            }
        }
        // Add last item
        CranQuery query = new CranQuery(queryId, queryBody);
        addQuery(query);

        System.out.println("<--- FINISHED Parsing " + path +" --->");
    }

    public void queryIndexFromQueries(Analyzer analyzer, Similarity similarity) throws IOException, ParseException {
        // Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

        isearcher.setSimilarity(similarity);
        System.out.println(isearcher.getSimilarity());

		QueryParser parser = new QueryParser("Body", analyzer);
        System.out.println("<----- STARTED Querying Index ----->");

        for (CranQuery cranQuery: getQueries()) {
            String searchTerm = removeSpecialChars(cranQuery.getContent());
            Query query = parser.parse(searchTerm);
            ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
            System.out.println("Query ID " + cranQuery.getId() + " complete");
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                SearchResult searchResult = new SearchResult(cranQuery.getId(), 
                    Integer.valueOf(hitDoc.get("ID")), i+1, hits[i].score, getScoringApproach());
                addResult(searchResult);
		    }
        }

		ireader.close();
		directory.close();
        System.out.println("<----- FINISHED Querying Index ----->");
    }

    public void saveToFile() throws IOException {
        String filePath = RESULTS_DIRECTORY + getScoringApproach() + ".test";
        File file = new File(filePath);

        if (file.exists()) {
            file.delete(); 
        }

        FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (SearchResult result: getResults()) {
            printWriter.println(result.toTrecEvalFormat());
        }
        printWriter.close();
        System.out.println("RESULTS SAVED to " + filePath);
    }

    public String getScoringApproach() {
        return this.scoringApproach;
    }

    public void setScoringApproach(String name) {
        this.scoringApproach = name;
    }

    public String removeSpecialChars(String query) {
        return query.replaceAll("\\?", "");
    }

    public void addQuery(CranQuery query) {
        this.queries.add(query);
    }

    public ArrayList<CranQuery> getQueries() {
        return this.queries;
    }

    public void addResult(SearchResult result) {
        this.results.add(result);
    }

    public ArrayList<SearchResult> getResults() {
        return this.results;
    }

    public boolean isIdTag(String line) {
        return line.startsWith(".I");
    }

    public boolean isNotBodyTag(String line) {
        return !line.startsWith(".W");
    }
}