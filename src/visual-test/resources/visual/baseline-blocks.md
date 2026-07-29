<!--
Image baseline: the block constructs - lists, tables, task lists, alerts, footnotes, images.

The image is a data URI on purpose: the container has no network, and a file the fixture points at would
make the picture depend on where the test was started from.
-->

# Lists

- item one
- item two
  - nested item
    - deeper still
- item three

1. first
2. second
   1. nested ordered
   2. and another

5. an ordered list starting at five
6. next

1) with parentheses
2) instead of dots

- a loose list

- has blank lines

- between its items

- an item with two paragraphs

  the second one

- an item holding a block

  ```
  of code
  ```

- [ ] an open task
- [x] a finished one
- [ ] a third, to see the spacing

# Tables

| Version | Changes         |
|---------|-----------------|
| v1.0.0  | Initial release |
| v1.0.1  | Bug fixes       |

| Left | Center | Right |
|:-----|:------:|------:|
| a    | b      | c     |
| longer cell | `inline code` | 42 |
|      | empty on the left | |

# Alerts

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

> [!NOTE]
> An alert of more than one paragraph.
>
> This is the second, and below it a list and a code block:
>
> - one
> - two
>
> ```bash
> mvn clean package
> ```

> [!UNKNOWN]
> A marker naming no alert stays an ordinary quote.

# Images

![an inline image](data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI5NiIgaGVpZ2h0PSI0OCI+PHJlY3Qgd2lkdGg9Ijk2IiBoZWlnaHQ9IjQ4IiBmaWxsPSIjMDk2OWRhIi8+PHRleHQgeD0iOCIgeT0iMjkiIGZpbGw9IiNmZmYiIGZvbnQtZmFtaWx5PSJzYW5zLXNlcmlmIiBmb250LXNpemU9IjE0Ij5pbWFnZTwvdGV4dD48L3N2Zz4=)

A [linked image](https://example.com/target) sits inline: ![the same picture](data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0OCIgaGVpZ2h0PSIyNCI+PHJlY3Qgd2lkdGg9IjQ4IiBoZWlnaHQ9IjI0IiBmaWxsPSIjMWE3ZjM3Ii8+PC9zdmc+)

# Footnotes

A statement needing a source[^1], another one[^note], and the first one again[^1].

[^1]: The first footnote.

[^note]: The second one, with **markup** and a [link](docs/page.md).
