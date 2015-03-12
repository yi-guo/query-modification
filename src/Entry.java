// Yi Guo, yg2389
// COMS E6111 Project 1
// February 25, 2015

import java.util.HashMap;
import java.util.ArrayList;

// Represent a single Bing search result
public class Entry {

	private int id;										// Entry number from 1 to 10
	private String url;									// Entry URL
	private String title;								// Entry title
	private String description;							// Entry description
	private boolean isRelevant;							// Stores if the entry is relevant
	private ArrayList<String> terms;					// Stores a list of terms in the entry with stop words eliminated
	private HashMap<String, Double> termFrequencies;	// Stores term frequencies in a string and double pair
	
	// Constructor
	public Entry(int id, String url, String title, String description) {
		this.id = id;
		this.url = url;
		this.title = title;
		this.isRelevant = false;
		this.description = description;
		this.terms = new ArrayList<String>();
		this.computeTermFrequencies();
	}
	
	// Compute term frequencies
	private void computeTermFrequencies() {
		termFrequencies = new HashMap<String, Double>();
		String[] terms = (title + " " + description).split(" ");
		for (String term : terms) {
			term = strip(term);		// Formalize terms, e.g. "bill," -> "bill"
			// Eliminate stop words and empty strings after formalization
			if (term.length() > 0 && !Main.stopWords.contains(term) && !isNumeric(term)) {
				this.terms.add(term);							// Count term occurrence first
				if (termFrequencies.containsKey(term))
					termFrequencies.put(term, termFrequencies.get(term) + 1.0);
				else
					termFrequencies.put(term, 1.0);
			}
		}
		// Compute term frequencies
		for (String term : termFrequencies.keySet())
			termFrequencies.put(term, termFrequencies.get(term) / termFrequencies.size());
	}
	
	// Formalize a term, e.g. "bill," -> "bill"
	private String strip(String term) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < term.length(); i++) {	// Keep terms such as "well-known", "I'll", and "7.5" so that last two can be correctly removed
			if (Character.isLetterOrDigit(term.charAt(i)) || term.charAt(i) == '-' || term.charAt(i) == 39 || term.charAt(i) == '.')
				builder.append(Character.toLowerCase(term.charAt(i)));
		}
		return builder.toString();
	}
	
	// Return true if a given term is numeric
	private boolean isNumeric(String term) {
		try {
			Double.parseDouble(term);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	// Public interface to get entry ID
	public int getId() {
		return id;
	}
	
	// Public interface to set relevance
	public void setRelevance(boolean relevance) {
		isRelevant = relevance;
	}
	
	// Public interface to get relevance
	public boolean isRelevant() {
		return isRelevant;
	}
	
	// Public interface to get term frequencies
	public HashMap<String, Double> getTermFrequencies() {
		return termFrequencies;
	}
	
	// Public interface to get a string representation of entry	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("\n******************** Result %d ********************\n", this.id));
		builder.append(String.format("URL: %s\nTitle: %s\nDescription: %s\n", this.url, this.title, this.description));
		return builder.toString();
	}
	
}
