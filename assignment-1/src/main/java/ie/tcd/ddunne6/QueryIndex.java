package ie.tcd.ddunne6;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

class QueryIndex {
    // the location of the search index
	private static String INDEX_DIRECTORY = "../index";
    // Limit the number of search results we get
	private static int MAX_RESULTS = 50;

    private ArrayList<CranQuery> queries = new ArrayList<CranQuery>();
    private ArrayList<SearchResult> results = new ArrayList<SearchResult>();

    public QueryIndex(String path) {
        try {
            parseCorpus(path);
            queryIndexFromQueries();
        } catch (Exception e) {
            //TODO: handle exception
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

    public void queryIndexFromQueries() throws IOException {
        // Open the folder that contains our search index
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
		
		// create objects to read and search across the index
		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		// builder class for creating our query
		BooleanQuery.Builder query = new BooleanQuery.Builder();
        System.out.println("running queries...");

        for (CranQuery cranQuery: getQueries()) {
            System.out.println("for each cranQuery");
            System.out.println("Searching for type Body with " + cranQuery.getContent());
            Query term = new TermQuery(new Term("Body", cranQuery.getContent()));
            query.add(new BooleanClause(term, BooleanClause.Occur.SHOULD));

            ScoreDoc[] hits = isearcher.search(query.build(), MAX_RESULTS).scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                System.out.println("\tfor each hit");
                Document hitDoc = isearcher.doc(hits[i].doc);
                SearchResult searchResult = new SearchResult(cranQuery.getId(), 
                    Integer.parseInt(hitDoc.get("ID")), i, hits[i].score);
                System.out.println(searchResult.toString());
		    }
        }
        /*

		Query term1 = new TermQuery(new Term("content", "raven"));
		Query term2 = new TermQuery(new Term("content", "lenore"));
		Query term3 = new TermQuery(new Term("content", "criticism"));

		// construct our query using basic boolean operations.
		query.add(new BooleanClause(term1, BooleanClause.Occur.SHOULD	));   // AND
		query.add(new BooleanClause(term2, BooleanClause.Occur.MUST));     // OR
		query.add(new BooleanClause(term3, BooleanClause.Occur.MUST_NOT)); // NOT

		// Get the set of results from the searcher
		ScoreDoc[] hits = isearcher.search(query.build(), MAX_RESULTS).scoreDocs;
		
		// Print the results
		System.out.println("Documents: " + hits.length);
		for (int i = 0; i < hits.length; i++)
		{
			Document hitDoc = isearcher.doc(hits[i].doc);
			System.out.println(i + ") " + hitDoc.get("filename") + " " + hits[i].score);
		}

        */

		// close everything we used
		ireader.close();
		directory.close();
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