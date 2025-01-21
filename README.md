[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=bugs)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=SchweizerischeBundesbahnen_markdown2html-maven-plugin&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=SchweizerischeBundesbahnen_markdown2html-maven-plugin)

# Markdown to HTML Maven Plugin

This Maven plugin uses GitHub API for converting Markdown to HTML.

## Build

This extension can be produced using maven:
```bash
mvn clean package
```

## Usage

This plugin can be used in a maven project by adding the following to the `pom.xml`:

```xml
    <build>
        <plugins>
            <plugin>
                <groupId>ch.sbb.maven.plugins</groupId>
                <artifactId>markdown2html-maven-plugin</artifactId>
                <version>${markdown2html-maven-plugin.version}</version>
                <executions>
                    <execution>
                        <id>readme.md-to-about.html</id>
                        <phase>generate-resources</phase>
                        <goals>
                            <goal>convert</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <inputFile>${project.basedir}/README.md</inputFile>
                    <outputFile>${project.basedir}/README.html</outputFile>
                    <tokenEnvVarName>GITHUB_TOKEN</tokenEnvVarName>
                    <failOnError>true</failOnError>
                    <generateHeadingIds>true</generateHeadingIds>
                    <excludeChapters>
                        <excludeChapter>## Build</excludeChapter>
                        <excludeChapter>## Installation to Polarion</excludeChapter>
                        <excludeChapter>## Changelog</excludeChapter>
                    </excludeChapters>
                    <relativeLinkPrefix>https://localhost:9090/</relativeLinkPrefix>
                    <openExternalLinksInNewTab>true</openExternalLinksInNewTab>
                    <removeLinesWithStrings>
                        <removeLinesWithString>https://sonarcloud.io/api/project_badges/</removeLinesWithString>
                    </removeLinesWithStrings>
                    <removeLinesUsingPatterns>
                        <removeLinesUsingPattern>(?m)^.*\[Quality Gate Status\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Bugs\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Code Smells\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Coverage\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Duplicated Lines \(%\)\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Lines of Code\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Reliability Rating\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Security Rating\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Maintainability Rating\].*(\R|)</removeLinesUsingPattern>
                        <removeLinesUsingPattern>(?m)^.*\[Vulnerabilities\].*(\R|)</removeLinesUsingPattern>
                    </removeLinesUsingPatterns>
                    <imageProcessingType>EMBED</imageProcessingType>
                </configuration>
            </plugin>
...
        </plugins>
    </build>
```
