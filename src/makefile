# define a makefile variable for the java compiler
#
JCC = javac

# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
#
default: bbst.class 

# this target entry builds the RedBlackTree class 
# the RedBlackTree.class file is dependent on the RedBlackTree.java file
# and the rule associated with theis entry gives the command to create it
#
RedBlackTree.class: RedBlackTree.java
	$(JCC) RedBlackTree.java -o best

# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
#
clean: 
	$(RM) *.class
