
Problem Running JUnit Ant Task Within Eclipse
==============================================
The Ant plugin that comes with Eclipse doesn't have the dependant jar file, junit.jar.

To fix this I...
 1. copied junit.jar into C:\eclipse\plugins\org.apache.ant_1.6.5\lib
 2. Go to Windows -> Preferences -> Ant -> Runtime
 3. In the "Ant Home Entries" list hit "Add External Jar" and select the junit.jar file just referenced
