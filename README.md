pomfromjar-maven-plugin
=======================
If converting a non maven project to maven the developer does not have to search for each individual dependency pom snippet on maven central as this plugin will generate the pom dependency information based on jar files specified in a local directory. This plugin is also useful for just converting a large amount of jar files to their corresponding Maven dependencies without having to manually search the maven repository.

This plugin converts a jar file into its corresponding Maven dependency information. The dependency information is then copied by user to the project pom file where the dependencies are then downloaded to user machine. This means the user is no longer required to manually search for a jar on search.maven.org (or similar) to find a dependency. This can save a great deal of time when searching for hundreds of jar file dependencies, as can occur when converting non maven projects to maven.
This also is less error prone than searching manually as have to rely on jar filename when finding the dependency wheras this plugin uses the checksum value of the jar file.

The core of this plugin operates by accessing the SHA-1 checksum of a given jar file and then invoking a REST service call on http://repository.sonatype.org/service/local/lucene/search?sha1= passing in the checksum as 'sha1' parameter which returns the dependency information.

To run, execute the jUnit test class PluginTest.java . 
