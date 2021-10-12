package ie.tcd.ddunne6;

public class App 
{
    public static void main( String[] args )
    {   
        switch(args[0]) {
            case "index":
                System.out.println("Creating Indexes...");
                CreateIndex createIndexes = new CreateIndex("../corpus/cran.all.1400");
                break;
            case "query":
                System.out.println("Querying Indexes...");
                QueryIndex makeQueries = new QueryIndex("../corpus/cran.qry");
                break;
            default:
                System.out.println("Invalid arguments");
        }
    }
}
