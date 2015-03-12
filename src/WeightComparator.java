// Yi Guo, yg2389
// COMS E6111 Project 1
// February 25, 2015

import java.util.HashMap;
import java.util.Comparator;

// A customized idf comparator
public class WeightComparator implements Comparator<String> {

	// Holds the idf values to be compared
	private HashMap<String, Double> tfidf;
	
	// Constructor
	public WeightComparator(HashMap<String, Double> tfidf) {
		this.tfidf = tfidf;
	}
	
	@Override
	// Compare according to given terms' idf values
	public int compare(String t1, String t2) {
		if (tfidf.get(t2) > tfidf.get(t1))
			return 1;
		else if (tfidf.get(t2) < tfidf.get(t1))
			return -1;
		else
			return 0;
	}
	
}
