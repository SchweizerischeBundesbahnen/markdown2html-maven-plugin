<!--
Fixture for the normalized parity test: everything from strict.md plus the constructs where GitHub
wraps the markup in site chrome (heading anchors, syntax highlighting, image links, nofollow,
footnotes). Both sides are put through GitHubHtmlNormalizer before they are compared.
Deliberately absent: emoji shortcodes and task lists - see GitHubParityTest for why.
-->

# Heading level 1

Intro paragraph with **bold**, _emphasis_, ~~strikethrough~~ and `inline code`.

## Heading level 2

A soft
break stays a newline, while a hard break  
does not.

### Übersicht: heading with a non-ASCII title

#### Heading level 4

##### Heading level 5

###### Heading level 6

## Lists

- item one
- item two
  - nested item

1. first
2. second

## Code

```java
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, <World> & 'you'!");
    }
}
```

```bash
mvn clean package
```

```
fenced code block without a language
```

    indented code block

## Table

| Version | Changes                |
|---------|------------------------|
| v1.0.0  | Initial release        |
| v1.0.1  | Fixes `inline code`    |

## Links and images

Absolute [link](https://example.com/page), relative [link](docs/page.md), anchor [link](#heading-level-1).

Bare autolink https://example.com/auto and bracketed <https://example.org> and mail foo@bar.com.

![relative image](img/x.png)

[![badge](https://example.com/badge.svg)](https://example.com/target)

## Quotes

> Quoted paragraph.

> [!NOTE]
> GitHub renders this as an alert on the site, but the API returns a plain blockquote.

## Footnotes

Statement needing a source[^1] and another one[^note].

[^1]: The first footnote.

[^note]: The second footnote.

## Raw HTML

<div align="center">
  Raw HTML block content
</div>

<details>
<summary>Click to expand</summary>

Hidden **content**.

</details>

---

Last paragraph.
