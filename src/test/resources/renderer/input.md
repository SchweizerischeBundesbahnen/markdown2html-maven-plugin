# Renderer fixture

Pins the rendering offline, so the guarantee does not depend on api.github.com being reachable.
The cross-check against GitHub itself is GitHubParityTest.

A soft
break must stay a newline - GitHub renders Markdown files that way, and the two trailing spaces below
are the only thing that makes a hard break  
like this one.

## GFM extensions

| Version | Changes         |
|---------|-----------------|
| v1.0.0  | Initial release |

~~Strikethrough~~ text.

- [ ] open task
- [x] finished task

Autolinked https://example.com/auto and <https://example.org> and foo@bar.com.

Footnote reference[^1]

[^1]: The footnote text.

## Escaping and raw HTML

```java
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, <World> & 'you'!");
    }
}
```

Ampersand & less-than < greater-than > and "double quotes" stay readable.

<div align="center">
  <img src="doc/img.png" alt="pic" width="100">
</div>

<!-- HTML comments are dropped, the way GitHub's sanitizer drops them -->

Link with a space: [x](<docs/my page.md>)

Non-ASCII: Übersicht, Façade, Настройки, 설정
