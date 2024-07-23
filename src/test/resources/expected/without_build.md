# Extension Name

## Installation to Polarion

To install the extension to Polarion `ch.sbb.polarion.extension.variants-facade-<version>.jar`
should be copied to `<polarion_home>/polarion/extensions/ch.sbb.polarion.extension.variants-facade/eclipse/plugins`
It can be done manually or automated using maven build:
```bash
mvn clean install -P install-to-local-polarion
```
For automated installation with maven env variable `POLARION_HOME` should be defined and point to folder where Polarion is installed.

Changes only take effect after restart of Polarion.

## Polarion configuration

After installation, the extension should be enabled in Polarion. This can be done in the Polarion Administration.

### Configuration 1
Lorem ipsum dolor sit amet, consectetur adipiscing elit.
Nullam nec purus nec nunc tincidunt ultricies.

### Configuration 2
Nullam nec purus nec nunc tincidunt ultricies.

#### Configuration 2.1

code block
```java
public class Test {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

#### Configuration 2.2

TBD

## Changelog

| Version | Changes                           |
|---------|-----------------------------------|
| v1.0.0  | Initial release                   |