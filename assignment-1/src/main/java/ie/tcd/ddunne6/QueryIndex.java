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
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

class QueryIndex {
    // the location of the search index
	private static String INDEX_DIRECTORY = "../index";
    // Limit the number of search results we get
	private static int MAX_RESULTS = 50;
    private static String RESULTS_DIRECTORY = "results/";

    private ArrayList<CranQuery> queries = new ArrayList<CranQuery>();
    private ArrayList<SearchResult> results = new ArrayList<SearchResult>();

    public QueryIndex(String path) {
        try {
            parseCorpus(path);
            queryIndexFromQueries();
            saveToFile("someFile.test");
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

        System.out.println(getQueries().get(0).toString());
        System.out.println(getQueries().get(1).toString());
        System.out.println(getQueries().get(223).toString());
        System.out.println(getQueries().get(224).toString());

        System.out.println("<--- FINISHED Parsing " + path +" --->");
    }

    public void queryIndexFromQueries() throws IOException, ParseException {
        // Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		
		// create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

        Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("Body", analyzer);
        System.out.println("<----- STARTED Querying Index ----->");

        for (CranQuery cranQuery: getQueries()) {
            String searchTerm = removeSpecialChars(cranQuery.getContent());
            //System.out.println("Formatted -> " + searchTerm);
            Query query = parser.parse(searchTerm);
            ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
            //System.out.println("Number of hits: " + Integer.toString(hits.length));
            //System.out.println("QueryID\tQ0\tDocID\tRank\tScore\t\tNote");
            System.out.println("Query ID " + cranQuery.getId() + " complete");
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                SearchResult searchResult = new SearchResult(cranQuery.getId(), 
                    Integer.valueOf(hitDoc.get("ID")), i+1, hits[i].score);
                addResult(searchResult);
		    }
        }

		ireader.close();
		directory.close();
        System.out.println("<----- FINISHED Querying Index ----->");
    }

    public void saveToFile(String fileName) throws IOException {
        File file = new File(RESULTS_DIRECTORY + fileName);

        if (file.exists()) {
            file.delete(); 
        }

        FileWriter fileWriter = new FileWriter(RESULTS_DIRECTORY + fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (SearchResult result: getResults()) {
            printWriter.println(result.toTrecEvalFormat());
        }
        printWriter.close();
        System.out.println("RESULTS SAVED to " + RESULTS_DIRECTORY + fileName);
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