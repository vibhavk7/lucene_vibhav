package ie.tcd.ddunne6;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * Hello world!
 *
 */
public class CreateIndexes
{
    private String corpusPath;

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "../index";

    public String getCorpusPath() {
        return corpusPath;
    }

    public void setCorpusPath(String path) {
        this.corpusPath = path;
    }

    public CreateIndexes(String path) {
        setCorpusPath(path);
        System.out.println("DEBUG " + getCorpusPath());
        try {
            parseCorpusAndIndex();
        } catch (Exception e) {
            //TODO: handle exception
        }

    }

    public void parseCorpusAndIndex() throws IOException {
        // Lucene Setup
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // To store an index in memory
        // Directory directory = new RAMDirectory();
        // To store an index on disk
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        
        // Index opening mode
        // IndexWriterConfig.OpenMode.CREATE = create a new index
        // IndexWriterConfig.OpenMode.APPEND = open an existing index
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
        // does not exist, otherwise it opens it
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        IndexWriter iwriter = new IndexWriter(directory, config);

        // File opening Setup
        FileInputStream fstream = new FileInputStream(getCorpusPath());
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        String currentTag = "ID"; // Assumption document starts with ID
        String previousTag = "";
        String contentOfTag = "";
        Boolean startNewContent = true;

        System.out.println("<--- STARTED Parsing " + getCorpusPath() +" --->");

        //Read File Line By Line
        while ((strLine = br.readLine()) != null)   {
            System.out.println("\tPARSE: " + strLine);
            if (isNewTag(strLine)) {
                previousTag = currentTag;
                currentTag = extractTagName(strLine);
                System.out.println("NEW TAG >> " + strLine);
                // currentTag will become equal to ID, Title, Author, Bibliography or Body
                if (isIdTag(strLine)) {
                    String docId = strLine.substring(3);
                    System.out.println("ID >> " + docId);
                }
                if(!"ID".equals(previousTag)) {
                    System.out.println("SAVING Previous " + previousTag + ": " + contentOfTag);
                }
                startNewContent = true;
            }
            else { // Need to buffer the content of the line
                if (startNewContent) {
                    contentOfTag = strLine;
                    startNewContent = false; 
                }
                else {
                    contentOfTag = contentOfTag + "\n" + strLine;
                }
            }
        }

        // Save the last tag
        System.out.println("SAVING Previous " + currentTag + ": " + contentOfTag);

        System.out.println("<--- FINISHED Parsing and Indexing --->");

        fstream.close();
    }

    public String extractTagName(String tag) {
        if (isIdTag(tag)) {
            return "ID";
        } 
        else if (isTitleTag(tag)) {
            return "Title";
        }
        else if (isAuthorTag(tag)) {
            return "Author";
        }
        else if (isBibTag(tag)) {
            return "Bibliography";
        }
        else {
            return "Body";
        }
    }

    public boolean isNewTag(String line) {
        return isIdTag(line) || isTitleTag(line) || isAuthorTag(line) || isBibTag(line) || isBodyTag(line);
    }

    public boolean isIdTag(String line) {
        return line.startsWith(".I");
    }

    public boolean isTitleTag(String line) {
        return line.startsWith(".T");
    }

    public boolean isAuthorTag(String line) {
        return line.startsWith(".A");
    }

    public boolean isBibTag(String line) {
        return line.startsWith(".B");
    }

    public boolean isBodyTag(String line) {
        return line.startsWith(".W");
    }
}
