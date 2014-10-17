import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MakeMergedBibTeX {

	public static String START_DIR = "";
	public static String BIB_OUT = "all_merged.bib";
	
	public static void main(String[] args) {
		if (args.length > 0) {
			START_DIR = args[0];
			BIB_OUT = args[1];
		}
		
		Set<String> bibTexs = new HashSet<String>();
		recurse(Paths.get(START_DIR), bibTexs);
		System.out.println("Found " + bibTexs.size() + " BibTeX files...");
		
		try {
			Files.delete(Paths.get(BIB_OUT));
		} catch (IOException e1) {}
		
		for (String fs : bibTexs) {
			System.out.println("Writing: " + fs);
			try {
				Files.write(Paths.get(BIB_OUT), Files.readAllBytes(Paths.get(fs)),
						StandardOpenOption.WRITE,
						StandardOpenOption.APPEND,
						StandardOpenOption.CREATE);	
			} catch (IOException e) {}
		}
	}

	private static void recurse(Path startFile, Set<String> bibTexs) {
		Path out = Paths.get(BIB_OUT);
		for (Path f : fileList(out)) {
			if (f.getFileName().toString().startsWith("."))
				continue;
			if (out.toString().equals(f.toString()))
				continue;
			if (Files.isDirectory(f))
				recurse(f, bibTexs);
			else if (f.getFileName().toString().toLowerCase().endsWith(".bib")) {
				System.out.println("Added: " + f);
				bibTexs.add(f.toString());
			}
		}
	}
	
	private static List<Path> fileList(Path directory) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {}
        return fileNames;
    }
}
