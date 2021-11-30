# lucene-cranfield-collection

Assignment from my Master's module "Information Retrieval and Web Search" where I index the Cranfield Collection and evaluate different performance metrics with trec eval.

## Prerequisites

Install and make [trec_eval](https://github.com/usnistgov/trec_eval)

## RECOMMENDED: Run the Entire Thing
First navigate to the *assignment-1/* directory.
There is a run.sh file that will build and execute the program which will print out the trec eval results afterwards.

```bash run.sh```

---

## Build
This will have the effect of installing all prerequisite packages e.g. lucene 8.6.2

```mvn package```

## Create Index with Standard Analyzer and Vector Space Model Scoring

```java -jar target/assignment-1-1.0-SNAPSHOT.jar <analyzer> <similarity>```

analyzer: `standard`,`english`, or `whitespace`

similarity: '`vsm`' with `bm25`

### Example with StandardAnalyzer & Vector Space Model
```java -jar target/assignment-1-1.0-SNAPSHOT.jar standard vsm```

## Trec Eval
```trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/StandardAnalyzerVSM.test```

We can replace the .test file with others from results/ directory

## Additional Info
corpus/
- cran.all - The documents
- cran.qry - The queries
- QRelsCorrectedforTRECeval - The relevance assesments (adjusted)
