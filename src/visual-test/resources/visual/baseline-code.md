<!--
Image baseline: the syntax colours. This is the only place they are checked at all - the live comparison
against GitHub has to switch them off, because Linguist and the local lexers cannot agree on them.

One block per lexer the plugin maps a language name to, so that a lexer changing what it makes of a token
shows up here.
-->

# Syntax colours

## java

```java
/** Javadoc with a @param tag */
@Override
public class Test extends Base implements Runnable {
    private static final int COUNT = 0x2A;
    public static void main(String[] args) {
        char c = 'x';
        double d = 1.5e3;
        System.out.println("Hello, <World> & 'you'!"); // a comment
        /* a block
           comment */
    }
}
```

## xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<plugin id="a" enabled='true'>
    <groupId>ch.sbb.maven.plugins</groupId><!-- a comment -->
    <![CDATA[raw content]]>&amp;
</plugin>
```

## bash

```bash
#!/usr/bin/env bash
# build it
set -euo pipefail
export POLARION_HOME="/opt/polarion"
mvn clean package -P install-to-local-polarion | grep -c 'BUILD'
if [[ -d "${POLARION_HOME}" ]]; then echo "found"; fi
```

## yaml

```yaml
# a comment
name: Build
on:
  push:
    branches: ['**']
env:
  JAVA_VERSION: 21
  QUOTED: "a string"
```

## json

```json
{
  "name": "value",
  "number": 42,
  "float": 1.5,
  "flag": true,
  "nothing": null,
  "list": [1, 2, 3]
}
```

## sql

```sql
-- a comment
SELECT id, name FROM users WHERE created_at > '2026-01-01' AND active = 1 ORDER BY name;
```

## python

```python
# a comment
from typing import List

def greet(names: List[str], count: int = 1) -> None:
    """A docstring."""
    for name in names:
        print(f"Hello, {name}!" * count)
```

## properties

```properties
# a comment
polarion.home=/opt/polarion
extension.enabled=true
```

## kotlin

```kotlin
// a comment
data class Point(val x: Int, val y: Int) {
    fun length(): Double = Math.sqrt((x * x + y * y).toDouble())
}
```

## javascript

```javascript
// a comment
const greet = (name = "world") => {
    console.log(`Hello, ${name}!`);
    return /a regex/.test(name);
};
```

## typescript

```typescript
interface Point { x: number; y: number }
export function distance(a: Point, b: Point): number {
    return Math.hypot(a.x - b.x, a.y - b.y);
}
```

## dockerfile

```dockerfile
FROM eclipse-temurin:21-jre
ENV POLARION_HOME=/opt/polarion
COPY target/*.jar /app/
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

## go

```go
package main

import "fmt"

func main() {
    const greeting = "Hello"
    fmt.Println(greeting, 42) // a comment
}
```

## rust

```rust
fn main() {
    let numbers = vec![1, 2, 3];
    println!("{:?}", numbers); // a comment
}
```

## css

```css
/* a comment */
.markdown-body pre {
    background-color: #f6f8fa;
    padding: 16px;
}
```

## ini

```ini
; a comment
[section]
key = value
```

## makefile

```makefile
# a comment
build:
	mvn clean package
```

## Without a language, and indented

```
a fence with no language stays plain: <html> & "quotes"
```

    an indented block stays plain too

## A language nothing maps to

```brainfuck
+++[->+++<]>.
```
