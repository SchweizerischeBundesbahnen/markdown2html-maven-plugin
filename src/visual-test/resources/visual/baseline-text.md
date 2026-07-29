<!--
Image baseline: everything that is text - headings, inline markup, links, quotes, breaks, entities, raw
HTML. Split from the other fixtures so that a failing picture names the area it is in, and so that no
recording grows past the size the large-file hook allows.
-->

# Heading level 1

## Heading level 2

### Heading level 3

#### Heading level 4

##### Heading level 5

###### Heading level 6

Setext heading
==============

Another one
-----------

## Inline markup

Plain, **bold**, _emphasis_, ***both***, **bold with _emphasis_ inside**, *a**b**c*, ~~strikethrough~~,
`inline code`, `` code with `backticks` ``, and `<html> & "quotes"` inside code.

A soft
break stays a newline, a hard break  
comes from two trailing spaces, and a backslash\
does the same.

Escapes: \*not emphasis\*, \_not emphasis\_, \`not code\`, \# not a heading.

## Links

Relative [link](docs/page.md), absolute [link](https://example.com/page), anchor
[link](#heading-level-1), [titled link](docs/page.md "The title"), [reference link][ref], and a link with
[a space in it](<docs/my page.md>).

[ref]: docs/ref.md

Autolinked: https://example.com/auto, <https://example.org>, and foo@bar.com.

## Quotes

> An ordinary quote.

> A quote of two paragraphs.
>
> This is the second.

> The outer quote
> > holds a nested one.

> - a quote holding a list
> - second item
>
> ```
> and a code block
> ```

## Entities and characters

Entities: &copy; &amp; &lt; &gt; &#x41; &nbsp; &hellip;

Ampersand & less-than < greater-than > and "double quotes" as written.

Non-ASCII: Übersicht, Façade, Настройки, 설정, ★ ✓ → ∑ ½ — –

## Raw HTML

<div align="center">
  A centred raw HTML block
</div>

Inline raw <b>bold</b>, <i>italic</i>, <code>code</code>, and a <br> line break.

<details>
<summary>Click to expand</summary>

Hidden **content** with a list:

- one
- two

</details>

---

The last paragraph, after a thematic break.
