<!--
Fixture for the image baselines: what the plugin produces, as a reader sees it. It leans on the things the
live comparison against GitHub cannot check - the syntax colours, which the neutralising stylesheet there
has to switch off, and the alerts, which the API mode that test uses does not render at all.
-->

# Heading level 1

Intro with **bold**, _emphasis_, ~~strikethrough~~, `inline code` and a [link](docs/page.md).

## Alerts

> [!NOTE]
> Useful information that users should know, even when skimming content.

> [!TIP]
> Helpful advice for doing things better or more easily.

> [!IMPORTANT]
> Key information users need to know to achieve their goal.

> [!WARNING]
> Urgent info that needs immediate user attention to avoid problems.

> [!CAUTION]
> Advises about risks or negative outcomes of certain actions.

## Syntax colours

```java
/** Javadoc @param args */
@Override
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, <World> & 'you'!"); // a comment
    }
}
```

```xml
<plugin>
    <groupId>ch.sbb.maven.plugins</groupId><!-- a comment -->
    <artifactId>markdown2html-maven-plugin</artifactId>
</plugin>
```

```bash
# build it
mvn clean package -P install-to-local-polarion
export POLARION_HOME="/opt/polarion"
```

```yaml
key: value
list:
  - first  # a comment
  - second
```

```
a fence with no language stays plain
```

## Table, lists, quote

| Version | Changes         |
|---------|-----------------|
| v1.0.0  | Initial release |
| v1.0.1  | Bug fixes       |

- item one
- item two
  - nested item

1. first
2. second

- [ ] open task
- [x] finished task

> An ordinary quote.

Footnote reference[^1]

[^1]: The footnote text.
