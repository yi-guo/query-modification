JFLAGS = -g
JVM = java
JC = javac
.SUFFIXES: .java .class
.java.class:
    $(JC) -classpath .:lib/org-apache-commons-codec.jar $(JFLAGS) $src/*.java

CLASSES = \
    src/Main.java \
    src/Entry.java \
    src/BingSearch.java \
    src/QueryExpansion.java \
    src/WeightComparator.java

MAIN = Main

default: classes

classes: $(CLASSES:.java=.class)

run: classes
    $(JVM) -classpath .:lib/org-apache-commons-codec.jar $(MAIN) $(KEY) $(PRECISION) $(QUERY)

clean:
    $(RM) *.class