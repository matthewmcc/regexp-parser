import java.io.File;
import java.io.IOException;

public class Regex {
    
    public static void main(String[] args) throws IOException {
        
        Parser pas = new Parser(args[0]);
        pas.parse();
        
        String workingDirectory = System.getProperty("user.dir");
        File fsm = new File(workingDirectory + File.separator + "output.fsm");
        File pat = new File(workingDirectory + File.separator + "pattern.txt");
        
        Searcher s = new Searcher(fsm, pat);
    }
}