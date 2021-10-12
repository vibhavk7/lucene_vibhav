package ie.tcd.ddunne6;

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
            switch(args[0]) {
                case "index":
                    System.out.println("Creating Index...");
                    CreateIndex createIndexes = new CreateIndex(CRANFIELD_DOCUMENT_PATH);
                    break;
                case "query":
                    System.out.println("Querying Index...");
                    QueryIndex makeQueries = new QueryIndex(CRANFIELD_QUERY_PATH);
                    break;
                default:
                    System.out.println("Invalid arguments");
            }
        }
    }
}
