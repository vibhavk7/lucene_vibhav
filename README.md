# lucene-cranfield-collection

## Build
This will have the effect of installing all prerequisite packages e.g. lucene 8.6.2

```mvn package```

## Create Index

```java -jar target/assignment-1-1.0-SNAPSHOT.jar index```

## Run Queries

```java -jar target/assignment-1-1.0-SNAPSHOT.jar query```




corpus/
- cran.all - The documents
- cran.qry - The queries
- QRelsCorrectedforTRECeval - The relevance assesments (adjusted)
