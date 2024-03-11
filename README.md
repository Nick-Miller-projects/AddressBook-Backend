# Address Book Backend XML to JSON converter

## Build the application
From the project root directory, run 
```./gradlew shadowJar```

## Usage
To run the application from the command line, run like so 
```java -jar build/libs/XMLConverter-all.jar <input> <output>```

## Example usage
```java -jar build/libs/XMLConverter-all.jar ab.xml abJSON.json```

And then returning to xml\
```java -jar build/libs/XMLConverter-all.jar abJSON.json abXML.xml```
