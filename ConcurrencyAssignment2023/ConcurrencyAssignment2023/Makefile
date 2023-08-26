JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class


SRCDIR=src/clubSimulation
BINDIR=bin
.java.class:
	$(JAVAC) $< 

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<


CLASSES=PeopleCounter.class CounterDisplay.class GridBlock.class PeopleLocation.class ClubGrid.class ClubView.class Clubgoer.class\
	AndreTheBarman.class ClubSimulation.class\


CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/clubSimulation/*.class

run: default
	java -cp $(BINDIR) clubSimulation.ClubSimulation $(ARGS)
