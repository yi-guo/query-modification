# query-modification
An information retrieval system that exploits user-provided relevance feedback to improve the search results returned by Bing.

## Submitted Files

    Source Code
    ==============================
    1) Main.java
    2) Entry.java
    3) BingSearch.java
    4) QueryExpansion.java
    5) WeightComparator.java

    External Libraries
    ==============================
    6) stopwords.txt
    7) org-apache-commons-codec.jar

    Test Results & Documentation
    ==============================
    10) README
    11) transcript.txt


----------------------------------------------------
2. Command Line Instructions
----------------------------------------------------

A well-written make file and shell script for automated compilation and execution are attached. To run, please follow the below steps.

    1) Unzip all the files
    2) Navigate to yg2389-proj1/
    3) Issue the following command

        $ sh run.sh <Bing account key> <precision> <query>

       For example, to query "gates" at a target precision of 0.9, simply issue

        $ sh run.sh Mi52d4RiLu5mnooSxjjVqaBNDl828jNk1y37isj9NYg 0.9 gates

       The makefile will automatically compile all the Java source code (if not yet compiled) and run the requested query.

    4) (Optional) To delete all the .class files when finished, simply issue

        $ make clean

3. Design and Implementation
----------------------------------------------------

Upon running, the program performs the following procedures.

    1) (Main.java) Read stop words such as "a", "an", "the" from stopwords.txt and store them in a hash set.
    2) (Main.java) Retrieve Bing search account key, target precision, and query as needed from the command line parameters.
    3) (BingSearch.java) Conduct Bing search on the query with the provided account key and receive the top 10 results in XML format.
    4) (BingSearch.java) Parse the XML results, retrieve URL, title, and description from each result entry, and return an array of entries (defined in Entry.java) of size 10.
    5) (Entry.java) For each entry returned, extract terms from title and description, formalize terms (e.g., "bill," -> "bill"), and compute term frequencies with stop words (constructed in step 1) eliminated.
    6) (Main.java) Display URL, title, and description for each entry and prompt user for relevance feedback.
    7) (Main.java) Compute precision, generate and display feedback summary; Terminate if precision is 0 or target precision accomplished.
    8) (QueryExpansion.java) Compute inverse document frequencies for all the 10 entries.
    9) (QueryExpansion.java) Compute modified query vector (with the formula given below).
    10) (QueryExpansion.java, WeightComparator.java) Sort the terms in the modified query vector in descending order according to their weight using the customized WeightComparator.
    11) (QueryExpansion.java) Extract the intial query terms plus the most two weighted new terms, generate and return a string of new query in the descending order of the term weight.
    12) Repeat step 3 to 12 with the new query until precision is 0 or target precision is accomplished.


4. Query Modification Method
----------------------------------------------------

The query modification in this program strictly follows Rocchio algorithm introduced in the lectures. The program applies the recommended constants in the Introduction to Information Retrieval textbook where alpha = 1, beta = 0.75, gamma = 0.15.

Specifically, a query is modified given the weights in the modified query vector computed following the algorithm. That is,

    Modified query vector = alpha * initial query vector + beta * sum(relevant document vector) / number of relevant documents - gamma * sum(non-relevant document vector) / number of non-relevant documents

To complete the above computation, we

    1) Compute the term frequencies for all the terms in each of the top 10 documents.

        tf(t, d) = number of occurences of t in d / size of d

    2) Compute the inverse document frequencies for all the terms in the top 10 documents.

        idf(t) = log(number of documents / number of documents containing t)

    3) Compute the modified query vector following the formula given above.
    4) Rank the terms in descending order with the weights computed in the modified query vector.
    5) Generate new query by extracting the initial query terms and the most two weighted terms; Keep their order in the sorted descending order of their weights.