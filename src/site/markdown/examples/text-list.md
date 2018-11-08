# Generating Property With Text Format Issue List

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

## POM File Content

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
            <url>TBD</url>
        </repositor>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <id>mriss-plugin-repository</id>
            <layout>default</layout>
            <name>mriss-repository</name>
            <url>TBD</url>
        </pluginRepository>
    </pluginRepositories>
</project>
```

## README.md Content

The README.md file content below contains a property ```issues.text.list``` which is the default
property name where the final issue list in markdown format will be set. This is configurable and
can be set to another name. See the new mojo configuration instructions [here](). 

```
# My README

## Release Notes

${issues.text.list}

```

## Site.xml Content

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
