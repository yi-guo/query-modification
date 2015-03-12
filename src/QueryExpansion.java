// Yi Guo, yg2389
// COMS E6111 Project 1
// February 25, 2015

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

// Represent a query expansion
public class QueryExpansion {
	
	private Entry[] results;										// Holds Bing search results
	private String[] initialQuery;									// Holds initial query terms
	private int numOfRelevantDocuments;								// Holds the number of relevant documents
	private HashMap<String, Double> newQueryVector;					// Holds the expanded query vector (term -> tf-idf)
	private HashMap<String, Double> inverseDocumentFrequencies;		// Holds the idf value for each term (term -> tf-idf)
	
	// Define alpha, beta, gamma as in Rocchio algorithm
	private final static double alpha = 1;
	private final static double beta = 0.8;
	private final static double gamma = 0.2;
	
	// Constructor
	public QueryExpansion(String[] initialQuery, Entry[] results) {
		this.results = results;
		this.initialQuery = initialQuery;
		this.numOfRelevantDocuments = 0;
		this.computeInverseDocumentFrequencies();
		this.computeNewQueryVector();
	}
	
	// Compute the inverse document frequencies for each term
	private void computeInverseDocumentFrequencies() {
		inverseDocumentFrequencies = new HashMap<String, Double>();
		for (Entry entry : results) {
			// Count the number of relevant documents by the way
			if (entry.isRelevant())
				numOfRelevantDocuments++;
			// idf(t) = log(total number of documents / number of documents containing t)
			for (String term : entry.getTermFrequencies().keySet())
				inverseDocumentFrequencies.put(term, Math.log(10.0 / getNumOfDocumentsContainingTerm(term)));
		}
	}
	
	// Return the number of documents containing the given term
	private int getNumOfDocumentsContainingTerm(String term) {
		int count = 0;
		for (Entry entry : results) {
			if (entry.getTermFrequencies().containsKey(term))
				count = count + 1;
		}
		return count;
	}
	
	// Compute the expanded query vector following Rocchio algorithm
	// new query vector = alpha * initial query vector + beta * sum(relevant document vector) / number of relevant documents -
	// 			gamma * sum(non-relevant document vector) / number of non-relevant documents
	private void computeNewQueryVector() {
		newQueryVector = new HashMap<String, Double>();
		for (Entry entry : results) {
			HashMap<String, Double> df = entry.getTermFrequencies();
			if (entry.isRelevant()) {		// Relevant document
				for (String term : df.keySet()) {
					if (newQueryVector.containsKey(term))
						newQueryVector.put(term, newQueryVector.get(term) +
								beta / numOfRelevantDocuments * df.get(term) * inverseDocumentFrequencies.get(term));
					else
						newQueryVector.put(term, beta / numOfRelevantDocuments * df.get(term) * inverseDocumentFrequencies.get(term));
				}
			} else {						// Non-relevant document
				for (String term : df.keySet()) {
					if (newQueryVector.containsKey(term))
						newQueryVector.put(term, newQueryVector.get(term) -
								gamma / (10 - numOfRelevantDocuments) * df.get(term) * inverseDocumentFrequencies.get(term));
					else
						newQueryVector.put(term, gamma / (10 - numOfRelevantDocuments) * df.get(term) * inverseDocumentFrequencies.get(term));
				}
			}
		}
		// Apply alpha * initial query vector
		for (String term : initialQuery)
			newQueryVector.put(term, newQueryVector.get(term) + alpha);
	}
	
	// Public interface to get the new query terms in a list
	public List<String> getNewQuery() {
		// Sort the terms in the new query vector by tf-idf
		List<String> sortedTerms = new ArrayList<String>(newQueryVector.keySet());
		Collections.sort(sortedTerms, new WeightComparator(newQueryVector));
		
		// Get new query terms starting from the most weighted
		int countNewQueryTerms = 0;
		List<String> newQuery = new ArrayList<String>();
		// Keeps adding terms until the number of terms in the new query is two more than the initial query
		for (int i = 0; newQuery.size() < initialQuery.length + 2; i++) {
			String term = sortedTerms.get(i);
			// If current term is a new term, add it only if there are fewer than two new terms
			if (!Arrays.asList(initialQuery).contains(term)) {
				if (countNewQueryTerms > 1)
					continue;
				countNewQueryTerms++;
			}
			newQuery.add(term);
		}
		return newQuery;
	}
	
}
