echo "Building Project"

mvn package

echo "Running Project with different scoring approaches"

java -jar target/assignment-1-1.0-SNAPSHOT.jar standard vsm
java -jar target/assignment-1-1.0-SNAPSHOT.jar standard bm25
java -jar target/assignment-1-1.0-SNAPSHOT.jar whitespace vsm
java -jar target/assignment-1-1.0-SNAPSHOT.jar whitespace bm25
java -jar target/assignment-1-1.0-SNAPSHOT.jar english vsm
java -jar target/assignment-1-1.0-SNAPSHOT.jar english bm25

echo "Results available in results/"
sleep 2
echo "Applying Trec Eval"

echo "English Analyzer with Vector Space Model"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/EnglishAnalyzerVSM.test
echo "English Analyzer with BM25"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/EnglishAnalyzerBM25.test
echo "Standard Analyzer with Vector Space Model"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/StandardAnalyzerVSM.test
echo "Standard Analyzer with BM25"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/StandardAnalyzerBM25.test
echo "Whitespace Analyzer with Vector Space Model"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/WhitespaceAnalyzerVSM.test
echo "Whitespace Analyzer with BM25"
trec_eval -m runid -m map -m gm_map -m P.5 ../corpus/QRelsCorrectedforTRECeval results/WhitespaceAnalyzerBM25.test