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

This Maven plugin converts Markdown to HTML.

Rendering happens locally, with [commonmark-java](https://github.com/commonmark/commonmark-java): CommonMark
plus the extensions GitHub Flavored Markdown adds on top of it — tables, strikethrough, task lists and
autolinks — plus footnotes. A single newline stays a newline rather than becoming a `<br>`, which is how
GitHub renders a Markdown *file* such as `README.md`.

Earlier versions posted the Markdown to the GitHub API instead. Converting locally removes the network call
from the build, along with its rate limits and its need for a token.

### Differences from github.com

The output is the same markup, without the chrome github.com wraps around it: headings carry no permalink
anchor (use `generateHeadingIds` for ids), fenced code blocks are not syntax-highlighted, images are not
proxied through camo and not wrapped in a link, external links get no `rel="nofollow"`, and `:emoji:`
shortcodes are left as written. GitHub's alerts (`> [!NOTE]`) render as ordinary blockquotes, which is also
what the GitHub API returns for them.

`GitHubParityTest` verifies the rest against the live GitHub API on every build: byte for byte on the
constructs GitHub leaves undecorated, and after stripping the chrome listed above on the ones it decorates.

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

`tokenEnvVarName` is deprecated and ignored - no GitHub token is needed anymore. It is still accepted, since
Maven fails on configuration it cannot map to a parameter, and can be dropped from the `pom.xml`.
