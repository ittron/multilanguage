# MultiLanguage

This Library is made for Ittron company in order to support multilanguage android application

## Getting Started
How to Implement

1. Add jitpack repository to your project gradle

```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

2. Add this line to your app gradle
```
dependencies {
    implementation 'com.github.ittron:multilanguage:1.2.5'
}
```

## Using Library
How to use library on your application

1. Initliaze the library first using code below, make sure to change domain to correct url.
```
import id.co.ittron.multilanguage.Language;
import id.co.ittron.multilanguage.LanguageBuilder;

languageBuilder = new LanguageBuilder(this);
languageBuilder.setAvailableLanguage(new String[] {"en","id"});
languageBuilder.setDownloadURL("http://domain/lang/pack");

Language.init(languageBuilder).build();
Language.setMainLanguage("en");
```
2. Use syntax below to load language string.
```
Language.getLanguage("Key")
```

To change language on runtime simply use
```
Language.setMainLanguage("en");
```
And restart activity

Make sure you have adding all available language before using it
