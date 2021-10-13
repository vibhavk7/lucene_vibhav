package ie.tcd.ddunne6;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class App 
{
    private static String CRANFIELD_DOCUMENT_PATH = "../corpus/cran.all.1400";
    private static String CRANFIELD_QUERY_PATH = "../corpus/cran.qry";

    public static void main( String[] args )
    {   
        if(args.length < 1) {
            System.out.println("Invalid arguments");
        }
        else {
            // argument 0 is standard/whitespace/english
            // argument 1 is vms/bm25
            String scoringApproach;
            Analyzer analyzer;
            Similarity similarity;

            switch(args[0]) {
                case "standard":
                    scoringApproach = "StandardAnalyzer";
                    analyzer = new StandardAnalyzer();
                    break;
                case "whitespace":
                    scoringApproach = "WhitespaceAnalyzer";
                    analyzer = new WhitespaceAnalyzer();
                    break;
                case "english":
                    scoringApproach = "EnglishAnalyzer";
                    analyzer = new EnglishAnalyzer();
                    break;
                default:
                    scoringApproach = "EnglishAnalyzer";
                    analyzer = new EnglishAnalyzer();
                    break;
            }

            switch(args[1]) {
                case "vsm":
                    similarity = new ClassicSimilarity();
                    scoringApproach = scoringApproach + "VSM";
                    break;
                case "bm25":
                    similarity = new BM25Similarity();
                    scoringApproach = scoringApproach + "BM25";
                    break;
                default:
                    similarity = new BM25Similarity();
                    scoringApproach = scoringApproach + "BM25";
                    break;
            }

            System.out.println("Creating Index...");
            CreateIndex createIndexes = new CreateIndex(CRANFIELD_DOCUMENT_PATH, analyzer);
            System.out.println("Querying Index...");
            QueryIndex makeQueries = new QueryIndex(CRANFIELD_QUERY_PATH,  analyzer, similarity, scoringApproach);
        }
    }
}
