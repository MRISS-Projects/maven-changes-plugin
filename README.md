
 <!--- Licensed to the Apache Software Foundation (ASF) under one --->
 <!--- or more contributor license agreements.  See the NOTICE file --->
 <!--- distributed with this work for additional information --->
 <!--- regarding copyright ownership.  The ASF licenses this file --->
 <!--- to you under the Apache License, Version 2.0 (the --->
 <!--- "License"); you may not use this file except in compliance --->
 <!--- with the License.  You may obtain a copy of the License at --->

 <!---   http://www.apache.org/licenses/LICENSE-2.0 --->

 <!--- Unless required by applicable law or agreed to in writing, --->
 <!--- software distributed under the License is distributed on an --->
 <!--- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY --->
 <!--- KIND, either express or implied.  See the License for the --->
 <!--- specific language governing permissions and limitations --->
 <!--- under the License. --->

 <!--- NOTE: For help with the syntax of this file, see: --->
 <!--- http://maven.apache.org/doxia/references/apt-format.html --->

[![Build Status](https://travis-ci.org/MRISS-Projects/maven-changes-plugin.svg?branch=master)](https://travis-ci.org/MRISS-Projects/maven-changes-plugin)
![altText](jacoco-badge/jacoco.svg "title")

# Maven Changes Plugin - Adapted Version

This is a forked version from [original maven changes plugin](https://github.com/apache/maven-changes-plugin) with two
new additions:

1. **Option mojo configuration to allow inclusion of issues closed in versions with -SNAPSHOT suffix:**
   Originally, changes mojo includes only issues closed in versions without -SNAPSHOT suffix. However,
   there might be cases where the release process needs to happen more quickly, having
   release without specific more detailed tests. At this cases, someone can decide to track 
   different SNAPSHOTS with build numbers assigned by a CI system, for example. A flag was inserted
   as a boolean to allow mojo to include issues in those versions. A issue to add this to the
   original maven changes plug-in as opened, and is described 
   [here](https://issues.apache.org/jira/browse/MCHANGES-388). Documentation on how to use the flag
   can be found at the mojo documentation [here](https://mriss-projects.github.io/maven-changes-plugin/github-report-mojo.html#removeSnapshotSuffix).
2. **A new mojo to generate na issue list in text format for [APT](https://maven.apache.org/doxia/references/apt-format.html)and [Markdown](https://guides.github.com/features/mastering-markdown/)**: This use case is useful if you have a README file for a project 
   with specific instructions and want to automatically
   insert a list of closed issues and a releases history at the end of the file. Alternative ways
   to do that can be found, but none of then would be easily attached to a maven build life cycle.
   More discussions about this topic follows below. The way this new mojo works is by connecting
   to the issue tracking system (tested just with github, so far), get the issues, produce a table
   using APT or Markdown format and set a build property with the resulting table. Thus, one might
   put a maven property following the standard `${my.property}` in a README file, filter it and will
   have the final README with the list. The mojo usage documentation can be found [here](https://mriss-projects.github.io/maven-changes-plugin/github-text-list-mojo.html).

## Discussion on New Mojo to Generate Issue List in Text Format

Alternative ways could be used to add list of issues in Markdown text. The obvious choice, for
github/gitlab at least, would be include a directive having a query for issues directly at a 
markdown file. It is not clear if this is possible, but even if it is, you would still need to 
attach such kind of trick to the maven build life cycle by invoking github/gitlab API to generate
the final result in HTML or PDF.

Another option would be to use the HTML version of issues list, generated by maven changes report
when generating a site. This would still need tricks to read and parse the HTML to add the list of
issues to a README file in text format, or add it to the end of another HTML being generated during
site generation.

Yet another option would e have README as a velocity macro and use issues list that would need
to be present in the velocity variables container during site or changes report generation. This
option would require more investigation, but would have the disadvantage of having README as
a velocity macro (.vm file).

The options are not very friendly, so I decided to add an new mojo to the changes plugin in order
to reuse the plugin code to grab issues from different sources.

### Example of Usage

Consider the following project structure:

```
pom.xml
src
 |
 +-- site
 |    |
 |    +-- markdown
 |         |
 |         +-- README.md
 |
 +-- site-desc
      |
      +-- site.xml
```

#### POM File Content

Use the pom below and replace the property ```github.project``` with the path to the issue tracking address for
your project.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0                       http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mygrupid</groupId>
    <artifactId>my-artifactId</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <properties>
        <!-- Set property below like: https://github.com/[MY-ORGANIZATION/MY-REPOSITORY]/issues -->
        <github.project></github.project>
    </properties>
    <build>
        <resources>
            <resource>
                <directory>src/site</directory>
                <includes>
                    <include>**/*.apt</include>
                    <include>**/*.md</include>
                </includes>
                <targetPath>${project.build.directory}/generated-site</targetPath>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/site</directory>
                <includes>
                    <include>**</include>
                </includes>
                <excludes>
                    <exclude>**/*.apt</exclude>
                    <exclude>**/*.md</exclude>
                </excludes>
                <targetPath>${project.build.directory}/generated-site</targetPath>
                <filtering>false</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-site-plugin</artifactId>
                <version>3.7</version>
                <configuration>
                    <siteDirectory>src/site-desc</siteDirectory>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-changes-plugin</artifactId>
                <version>2.12.3</version> <!-- The POM need to have the additional repository with the adapted plugin version -->
                <executions>
                    <execution>
                        <id>generate-list-of-issues</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>github-text-list</goal>
                        </goals>
                        <configuration>
                            <includeOpenIssues>false</includeOpenIssues>
                            <onlyMilestoneIssues>true</onlyMilestoneIssues>
                            <columnNames>Id,Type,Summary,Assignee,Reporter,Updated</columnNames>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pdf-plugin</artifactId>
                <version>1.4</version>
                <executions>
                    <execution>
                        <id>pdf-generation</id>
                        <phase>prepare-package</phase>
                        <goals>
                            <goal>pdf</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/site/pdf</outputDirectory>
                            <workingDirectory>${project.build.directory}/site/pdf</workingDirectory>
                            <aggregate>false</aggregate>
                            <includeReports>false</includeReports>
                            <siteDirectory>${project.build.directory}/generated-site</siteDirectory>
                        </configuration>
                        <inherited>false</inherited>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-changes-plugin</artifactId>
                <version>2.12.3</version>
                <configuration>
                    <includeOpenIssues>false</includeOpenIssues>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-project-info-reports-plugin</artifactId>
                <version>2.8</version>
                <configuration>
                    <dependencyLocationsEnabled>false</dependencyLocationsEnabled>
                </configuration>
            </plugin>
    </plugins>
    </reporting>
    <issueManagement>
        <system>github</system>
        <url>${github.project}</url>
    </issueManagement>
    <repositories>
        <repository>
            <id>mriss-repository</id>
            <layout>default</layout>
            <name>mriss-repository</name>
            <url>https://raw.github.com/MRISS-Projects/maven-repo/master</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>mriss-plugin-repository</id>
            <layout>default</layout>
            <name>mriss-plugin-repository</name>
            <url>https://raw.github.com/MRISS-Projects/maven-repo/master</url>
        </pluginRepository>
    </pluginRepositories>
</project>
```

#### README.md Content

The README.md file content below contains a property ```issues.text.list``` which is the default
property name where the final issue list in markdown format will be set. This is configurable and
can be set to another name. See the new mojo configuration instructions [here](https://mriss-projects.github.io/maven-changes-plugin/github-text-list-mojo.html#issueListPropertyName). 

```
# My README

## Release Notes

${issues.text.list}

```

#### Site.xml Content

```xml
<?xml version='1.0' encoding='UTF-8'?>
<project name="${project.name}" combine.self="override">
    <version position="right" />
    <publishDate position="right" format="yyyy-MM-dd - hh:mm:ss" />
    <body>
        <menu ref="reports" />
    </body>
</project>
```

#### Command

At command prompt at the root where pom.xml resides, run:

```sh
mvn clean install site
```

The results will be at `target/site/README.html` and `target/site/pdf/README.pdf`. At
`target/generated-site/markdown` you can see the filtered version of `README.md` with
the the variable replaced by a markdown table having the list of issues.

#### Live Example

An example with a list of issues in markdown format can be found in the README at
[this link](https://github.com/MRISS-Projects/dsh#release-notes).

### Maven Repository Where to Find the Adapted Plugin Version

The repository where to find the adapted versions is located at:
`https://raw.github.com/MRISS-Projects/maven-repo/master`. The POM coordinates are:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-changes-plugin</artifactId>
    <version>2.12.3</version>
</plugin>
```