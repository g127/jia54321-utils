# jia54321-utils

# make clean
mvn clean eclipse:clean idea:clean

# make install
mvn clean install -Dmaven.javadoc.skip=true  -Dmaven.test.skip=true
# make eclipse
mvn eclipse:clean eclipse:eclipse -DdownloadSources -DdownloadJavadocs 

# make idea
mvn  idea:idea