// Yi Guo, yg2389
// COMS E6111 Project 1
// February 25, 2015

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.apache.commons.codec.binary.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// Represent a Bing search session
public class BingSearch {

	private String key;			// Holds the account key for authentication
	private String query;		// Holds the query terms as a single string separated by spaces
	private Entry[] results;	// Holds the search results in Entry type

	// Constructor
	public BingSearch(String key, String query) {
		this.key = key;
		this.query = query.replace(" ", "%20");
		this.results = new Entry[10];
	}

	// Public interface to get the search URL
	public String getSearchURL() {
		return "https://api.datamarket.azure.com/Bing/Search/Web?Query=%27" + query + "%27&$top=10&$format=Atom";
	}

	// Public interface to get the search results
	public Entry[] getSearchResults() throws IOException, ParserConfigurationException, SAXException {
		
		// Conduct Bing search
		byte[] accountKey = Base64.encodeBase64((key + ":" + key).getBytes());
		String accountKeyEnc = new String(accountKey);
		URL url = new URL(getSearchURL());
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
		InputStream inputStream = (InputStream) connection.getContent();
		byte[] contentRaw = new byte[connection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);
		
		// Parse the returned search result in XML for URL, title, and description
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(content)));
		NodeList entries = document.getDocumentElement().getElementsByTagName("m:properties");
		// For each of the top 10 search results, retrieve their URL, title, and description
		for (int i = 0; i < entries.getLength(); i++) {
			NodeList entry = entries.item(i).getChildNodes();
			String link = entry.item(4).getTextContent();
			String title = entry.item(1).getTextContent();
			String description = entry.item(2).getTextContent();
			results[i] = new Entry(i + 1, link, title, description);
		}
		return results;
	}

}
