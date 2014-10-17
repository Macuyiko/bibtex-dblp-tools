import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class DBLPUtils {
	public static final String DBLP_SEARCH = "http://www.dblp.org/search/api/?q={Q}&h=1000&c=4&f=0&format=xml";
	public static final String DBLP_RECORD = "http://www.dblp.org/rec/bibtex/{I}";
	public static final String DBLP_BIBTEX = "http://dblp.uni-trier.de/rec/bib2/{I}.bib";
	
	
	/**
	 * Performs a search on DBLP, returns a list of parsed results
	 */
	public static List<String[]> getDblpSearch(String query) {
		List<String[]> ret = new ArrayList<String[]>();
		URL u = null;
		try {
			String url = DBLP_SEARCH.replace("{Q}", URLEncoder.encode(query, "UTF-8"));
			u = new URL(url);
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		
		try (BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()))) {
			String c = "";
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				c += inputLine + System.lineSeparator();
			in.close();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(new InputSource(
					new ByteArrayInputStream(c.getBytes("utf-8"))));
			NodeList nl = dom.getElementsByTagName("hit");
			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element el = (Element) nl.item(i);
					ret.add(parseDblpResultHit(el));
				}
			}
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parses a DBLP result element, returns {id, title, author, type, year}
	 */
	private static String[] parseDblpResultHit(Element el) {
		NodeList nl;
		String[] r = new String[] { "", "", "", "", "" };
		nl = el.getElementsByTagName("url");
		if (nl != null && nl.getLength() > 0)
			r[0] = ((Element) nl.item(0)).getTextContent().replace(
					"http://www.dblp.org/rec/bibtex/", "");
		nl = el.getElementsByTagName("title");
		if (nl != null && nl.getLength() > 0)
			r[1] = ((Element) nl.item(0)).getTextContent().trim();
		nl = el.getElementsByTagName("author");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element ael = (Element) nl.item(i);
				r[2] += ael.getTextContent().trim();
				if (i < nl.getLength() - 1)
					r[2] += ", ";
			}
		}
		nl = el.getElementsByTagName("type");
		if (nl != null && nl.getLength() > 0)
			r[3] = ((Element) nl.item(0)).getTextContent().trim();
		nl = el.getElementsByTagName("year");
		if (nl != null && nl.getLength() > 0)
			r[4] = ((Element) nl.item(0)).getTextContent().trim();
		return r;
	}

	/**
	 * Get BibTeX entry for a DBLP id
	 */
	public static String getDblpBibTex(String id) throws IOException {
		String response = "";
		while (response.equals("")) {
			id = id.replace("DBLP:", "");
			String url = DBLP_BIBTEX.replace("{I}", id);
			try {
				response = "";
				URL u = new URL(url);
				BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response += inputLine + System.lineSeparator();
				}
				in.close();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.err.print("Timeout... sleeping ten seconds and trying again");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {}
		}
		return response;
	}
}
