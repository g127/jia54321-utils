# jia54321-utils

# make clean
mvn clean eclipse:clean idea:clean

# make install
mvn clean install -Dmaven.javadoc.skip=true  -Dmaven.test.skip=true
# make eclipse
mvn eclipse:clean eclipse:eclipse -DdownloadSources -DdownloadJavadocs 

# make idea
mvn  idea:idea


# 包的的坐标信息

## 第1种
mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout

## 第2种
mvn -Dexec.executable='echo' -Dexec.args='version=${project.version}' --non-recursive exec:exec -q

## 三维
echo -e `mvn -f pom.xml -Dexec.executable='echo' -Dexec.args='name=${project.name}\\ngroupId=${project.groupId}\\nartifactId=${project.artifactId}\\nversion=${project.version}' --non-recursive exec:exec -q`
