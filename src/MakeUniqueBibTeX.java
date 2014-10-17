import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MakeUniqueBibTeX {

	public static String BIB_IN = "";
	public static String BIB_OUT = "";
	public static boolean CONSIDER_KEY = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			BIB_IN = args[0];
			BIB_OUT = args[1];
			CONSIDER_KEY = args[2].equals("true");
		}
		
		List<String> lines = Files.readAllLines(Paths.get(BIB_IN), Charset.forName("UTF-8"));
		boolean readingEntry = false;
		Set<String> bibTexs = new HashSet<String>();
		Files.delete(Paths.get(BIB_OUT));
		String entry = "";
		String comparisonEntry = "";
		
		for (String line : lines) {
			System.out.print(".");
			if (line.startsWith("@"))
				readingEntry = true;
			if (readingEntry) {
				if (CONSIDER_KEY || !line.startsWith("@"))
					comparisonEntry += line;
				entry += line + System.lineSeparator();
				if (line.equals("}")) {
					if (!bibTexs.contains(comparisonEntry)) {
						bibTexs.add(comparisonEntry);
						entry += entry + System.lineSeparator();
						Files.write(Paths.get(BIB_OUT), entry.getBytes("utf-8"), 
								StandardOpenOption.APPEND, 
								StandardOpenOption.CREATE, 
								StandardOpenOption.WRITE);
					}
					readingEntry = false;
					entry = "";
					comparisonEntry = "";
				}
			} else {
				line += line + System.lineSeparator();
				Files.write(Paths.get(BIB_OUT), line.getBytes("utf-8"), 
						StandardOpenOption.APPEND, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.WRITE);
			}
		}
		System.out.println();
	}

}
