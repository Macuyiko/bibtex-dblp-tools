import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MakeDBLPBibTeX {
	public static final BufferedReader IBR = new BufferedReader(new InputStreamReader(System.in));
	
	public static String BIB_IN = "in.bib";
	public static String BIB_OUT = "out.bib";
	public static boolean justCopyDblp = false;
	public static boolean justCopyNonDblp = false;

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			BIB_IN = args[0];
			BIB_OUT = args[1];
			justCopyDblp = args[2].equals("true");
			justCopyNonDblp = args[3].equals("true");
		}
		
		Path f = Paths.get(BIB_OUT);
		try {
			Files.delete(f);
		} catch (IOException e1) {}

		List<String> entries = getEntries();

		for (int ec = 0; ec < entries.size(); ec++) {
			String entry = entries.get(ec);
			System.out.println("* " + ec + "/" + entries.size() + " " + getKey(entry));
			if (getKey(entry).startsWith("DBLP:")) {
				System.out.println("  --> Already DBLP keyed");
				if (justCopyDblp) {
					System.out.println("  --> Copying existing");
					Files.write(f, entry.getBytes("utf-8"), 
							StandardOpenOption.APPEND, 
							StandardOpenOption.CREATE, 
							StandardOpenOption.WRITE);
				} else {
					System.out.println("  --> Refetching");
					String newEntry = DBLPUtils.getDblpBibTex(getKey(entry));
					if (newEntry == null)
						throw new Exception("Response was null");
					Files.write(f, newEntry.getBytes("utf-8"), 
							StandardOpenOption.APPEND, 
							StandardOpenOption.CREATE, 
							StandardOpenOption.WRITE);
				}
			} else if (justCopyNonDblp) {
				System.out.println("  --> Copying non-DBLP");
				Files.write(f, entry.getBytes("utf-8"), 
						StandardOpenOption.APPEND, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.WRITE);
			} else {
				String answer = getUserAnswer(entry);
				if (answer.equals("=")) {
					System.out.println("  --> Just copying");
					Files.write(f, entry.getBytes("utf-8"), 
							StandardOpenOption.APPEND, 
							StandardOpenOption.CREATE, 
							StandardOpenOption.WRITE);
				} else {
					System.out.println("  --> Fetching: " + answer);
					String newEntry = DBLPUtils.getDblpBibTex(answer);
					if (newEntry == null)
						throw new Exception("Response was null");
					Files.write(f, newEntry.getBytes("utf-8"), 
							StandardOpenOption.APPEND, 
							StandardOpenOption.CREATE, 
							StandardOpenOption.WRITE);
				}
			}
		}
	}

	private static String getUserAnswer(String entry) throws IOException {
		String answer = null;
		String query = getAttribute("title", entry);
		List<String[]> possibilities = null;
		while (answer == null) {
			System.out.println("       " + "TITLE: " + getAttribute("title", entry));
			System.out.println("       " + "AUTHORS: " + getAttribute("author", entry));
			System.out.println("       " + "YEAR: " + getAttribute("year", entry));
			if (query != null)
				possibilities = DBLPUtils.getDblpSearch(clean(query));
			String def = "*";
			System.out.println("     " + "[=]" + " Just copy current entry");
			System.out.println("     " + "[*]" + " Try a new query");
			if (possibilities != null && possibilities.size() > 0) {
				def = "0";
				for (int i = 0; i < possibilities.size(); i++) {
					System.out.println("     " + "["+i+"]" + " " + 
							possibilities.get(i)[0] + " " + 
							possibilities.get(i)[3] + " " + 
							possibilities.get(i)[4]);
					System.out.println("          " + possibilities.get(i)[1] + " " + possibilities.get(i)[2]);
				}
			}
			System.out.print("     " + " Your selection (" + def + "): ");
			answer = IBR.readLine();
			if (answer.trim().equals(""))
				answer = def;
			if (answer.equals("*")) {
				System.out.print("     " + " Enter new query: ");
				query = IBR.readLine();
				answer = null;
			} else if(answer.equals("=")) {
				break;
			} else {
				try{
					answer = possibilities.get(Integer.parseInt(answer))[0];
				} catch (NumberFormatException e) {
					answer = null;
				}
				break;
			}
		}
		return answer;
	}

	public static String clean(String s) {
		s = s.toLowerCase().trim();
		s = s.replaceAll("[^A-Z a-z]+", " ");
	//	s = s.replaceAll("\\b[\\w']{1,3}\\b", "");
		s = s.replaceAll("\\s{2,}", " ");
		return s;
	}

	public static String getKey(String entry) {
		String[] lines = entry.split("\n");
		for (String line : lines) {
			line = line.trim();
			if (line.startsWith("@"))
				return line.substring(line.indexOf("{") + 1, line.length() - 1);
		}
		return null;
	}

	public static String getAttribute(String attribute, String entry) {
		String[] lines = entry.split("\n");
		String total = "";
		boolean found = false;
		for (String line : lines) {
			line = line.trim();
			if (line.toLowerCase().startsWith(attribute.toLowerCase())) {
				found = true;
				line = line.substring(line.indexOf("{"));
			}
			if (found) {
				total += line + " ";
				if (line.trim().endsWith("},"))
					break;
			}
		}
		if (!found)
			return null;
		total = total.replace("\n", "").replace("\r", "").replace("\t", "");
		while (total.contains("  "))
			total = total.replace("  ", " ");
		total = total.trim();
		total = total.substring(1, total.length() - 2);
		return total;
	}

	public static List<String> getEntries() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(BIB_IN));
		boolean readingEntry = false;
		List<String> bibTexs = new ArrayList<String>();
		String entry = "";

		for (String line : lines) {
			if (line.startsWith("@"))
				readingEntry = true;
			if (readingEntry) {
				entry += line + System.lineSeparator();
				if (line.equals("}")) {
					bibTexs.add(entry);
					readingEntry = false;
					entry = "";
				}
			}
		}

		return bibTexs;
	}

}
