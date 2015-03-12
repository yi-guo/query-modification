JFLAGS = -g
JVM = java
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) -classpath .:org-apache-commons-codec.jar $(JFLAGS) $*.java

CLASSES = \
	Main.java \
	Entry.java \
	BingSearch.java \
	QueryExpansion.java \
	WeightComparator.java

MAIN = Main

default: classes

classes: $(CLASSES:.java=.class)

run: classes
	$(JVM) -classpath .:org-apache-commons-codec.jar $(MAIN) $(KEY) $(PRECISION) $(QUERY)

clean:
	$(RM) *.class