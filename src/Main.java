// Yi Guo, yg2389
// COMS E6111 Project 1
// February 25, 2015

import java.util.List;
import java.util.HashSet;
import java.util.Scanner;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

/* 
 * To access reference implementation, issue
 * $ ssh yg2389@clic.cs.columbia.edu
 * $ /home/gravano/6111/Html/Proj1/run.sh Mi52d4RiLu5mnooSxjjVqaBNDl828jNk1y37isj9NYg <precision> <query>
 *
 * To run this program, please compile and issue
 * $ java Main <account key> <precision> <query>
 * 
 */

public class Main {

    // Store stop words such as ["a", "an", "the", ... ] that should not count
    public static final HashSet<String> stopWords = new HashSet<String>();
    
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        
        int round = 0;      // Keep track of the number of rounds so far
        readStopWords();    // Read stop words from stopwords.txt
                
        // Retrieve account key, initial query, and target precision from command line parameters
        String key = args[0], query = args[2];
        double precision = 0, targetPrecision = Double.parseDouble(args[1]);
        
        // Create transcript.txt as required
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("transcript.txt", true)));
        
        // Keep searching and revising until target precision is achieved
        while (precision < targetPrecision) {
            round = round + 1;
            writer.println(String.format("==================== Round %d =====================\n", round));
            writer.println(String.format("==> Qurey: %s", query));
            System.out.println("Parameters:");
            System.out.println("Key: " + key);
            System.out.println("Query: " + query);
            System.out.println(String.format("Target Precision: %.1f", targetPrecision));
            
            // Conduct bing search with account key and initial query term(s)
            BingSearch search = new BingSearch(key, query);
            System.out.println(String.format("URL: %s", search.getSearchURL()));
            
            // Retrieve top 10 results from the search and store them in an array of entries
            Entry[] results = search.getSearchResults();
            System.out.println(String.format("Number of Results: %d", results.length));
            
            // Prompt relevance feedback from user and calculate precision
            precision = promptRelevanceFeedback(results, writer);
            writer.println(String.format("\n==> Precision: %.1f\n\n\n", precision));
            System.out.println(getFeedbackSummary(query, precision, targetPrecision));
            
            // Terminate if precision is 0
            if (precision == 0)
                break;
            
            // Expand query following Rocchio algorithm and generate new query
            QueryExpansion newQuery = new QueryExpansion(query.split(" "), results);
            query = join(newQuery.getNewQuery(), " ");
        }
        writer.println("\n\n\n\n");
        writer.close();
    }

    @SuppressWarnings("resource")
    // Read stop words from stopwords.txt and store them in the globally accessible hash table created above
    public static void readStopWords() throws IOException {
        String word;
        BufferedReader reader = new BufferedReader(new FileReader("stopwords.txt"));
        while ((word = reader.readLine()) != null) {
            stopWords.add(word);
        }
    }
    
    @SuppressWarnings("resource")
    // Iterate the top 10 results, display URL, title, and description, and prompt relevance feedback
    public static double promptRelevanceFeedback(Entry[] entries, PrintWriter writer) {
        Scanner in = new Scanner(System.in);
        int count = 0;
        for (Entry entry : entries) {
            writer.println(entry);
            System.out.println(entry);
            System.out.print("Relevant (Y/N)? ");
            String relevance = in.nextLine();
            while (!relevance.equalsIgnoreCase("Y") && !relevance.equalsIgnoreCase("N")) {
                System.out.print("Invalid relevance feedback. Please enter Y or N: ");
                relevance = in.nextLine();
            }
            if (relevance.equalsIgnoreCase("Y")) {      // Relevant
                count = count + 1;
                entry.setRelevance(true);
            }
            writer.println(String.format("Relevant? %s", entry.isRelevant()));
            writer.println("**************************************************");
        }
        return (double) count / 10;
    }
    
    // Generate feedback summary upon user's relevance feedback
    public static String getFeedbackSummary(String query, double precision, double targetPrecision) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n================ Feedback Summary ================\n");
        builder.append(String.format("Query: %s\nPrecision: %.1f\n", query, precision));
        if (precision == 0)
            builder.append("No relevant feedback. Exiting...\n");
        else if (precision < targetPrecision)
            builder.append("Below target precision. Augumenting...\n");
        else
            builder.append("Target precision accomplished. ALL DONE.\n");
        builder.append("==================================================\n");
        return builder.toString();
    }
    
    // Join the terms in the given list and separate them with the provided delimiter
    public static String join(List<String> query, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < query.size(); i++) {
            builder.append(query.get(i));
            if (i != query.size() - 1)
                builder.append(delimiter);
        }
        return builder.toString();
    }
    
}
