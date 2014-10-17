import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class MakeDBLPQueryBibTeX {
	public static final BufferedReader IBR = new BufferedReader(new InputStreamReader(System.in));
	
	public static String QUERY = "Seppe vanden Broucke";
	public static String BIB_OUT = "dblp_out.bib";
	
	public static void main(String[] args) throws Exception {
		
		if (args.length > 0) {
			QUERY = args[0];
			BIB_OUT = args[1];
		}
		
		List<String[]> possibilities = DBLPUtils.getDblpSearch(QUERY);
		if (possibilities == null) {
			System.err.println("Could not retrieve possibilities for your query");
			System.exit(1);
		}
		
		System.out.println("Adding "+possibilities.size()+" entries");
		
		for (int i = 0; i < possibilities.size(); i++) {
			System.out.println("["+i+"]\t" + 
					possibilities.get(i)[0] + ", " + 
					possibilities.get(i)[3] + ", " + 
					possibilities.get(i)[4]);
			System.out.println("\t" + possibilities.get(i)[1] + ", " + possibilities.get(i)[2]);
			String newEntry = DBLPUtils.getDblpBibTex(possibilities.get(i)[0]);
			if (newEntry == null)
				System.err.println("Response was null");
			Files.write(Paths.get(BIB_OUT), newEntry.getBytes("utf-8"), 
					StandardOpenOption.APPEND, 
					StandardOpenOption.CREATE, 
					StandardOpenOption.WRITE);
		}
		
		System.out.println("Removing duplicate entries...");
		MakeUniqueBibTeX.BIB_IN = BIB_OUT;
		MakeUniqueBibTeX.BIB_OUT = BIB_OUT;
		MakeUniqueBibTeX.CONSIDER_KEY = true;
		MakeUniqueBibTeX.main(new String[]{});
		
		System.out.println("All done, saved to: "+Paths.get(BIB_OUT));
	}

	


}
