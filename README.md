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
                </configuration>
            </plugin>
...
        </plugins>
    </build>
```
