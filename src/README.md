# Aho-Corasick vs KMP

This Java project compares Aho-Corasick with running KMP separately for each pattern.

## Run

```bash
javac Main.java
java Main
```

The program prints CSV-style results for 9 test cases: 10, 100, and 1000 patterns against texts of length 1000, 10000, and 100000. Each test runs 5 times. The average is measured using `System.nanoTime()` in milliseconds. Aho-Corasick time includes trie construction, failure links, and searching. KMP time includes LPS construction and searching for each pattern. Both methods count overlapping matches and duplicate patterns.

## How it works

A trie stores all patterns together, sharing common prefixes. Failure links point to the longest available suffix state when the next character cannot be followed. Each node also stores matching pattern IDs, including matches inherited from its failure state. The search moves through the text once and counts all outputs.

## Complexity

Let `P` be the sum of pattern lengths, `N` the text length, `K` the number of patterns, and `Z` the number of matches. Trie construction takes O(P) expected time using hash maps. Failure-link construction and copying output lists take O(P + C) expected time, where C is the total number of copied output entries. Searching takes O(N + Z) time when reporting matches, or O(N) for counting matches using the stored output-list sizes. Space is O(P + C). In cases with many overlapping suffix patterns, C can be large.

KMP takes O(N + m) time for one pattern of length m, so searching K patterns separately takes O(KN + P) time and O(max pattern length) extra space when processed one by one.

## Discussion

Aho-Corasick is useful for searching many patterns in the same text because the text is scanned once. KMP is simpler and may be faster for only a few patterns because it has less setup overhead. Aho-Corasick uses more memory for its trie, failure links, and output lists.

## Results

Example measurements from one run (milliseconds; averages of 5 runs):

```csv
Patterns,Text length,Aho avg ms,KMP avg ms,Matches
10,1000,1.768,0.992,0
10,10000,1.964,1.668,14
10,100000,2.776,5.629,114
100,1000,0.452,0.472,29
100,10000,0.671,7.812,281
100,100000,2.623,62.423,2687
1000,1000,1.912,4.608,329
1000,10000,2.748,60.045,3108
1000,100000,3.573,551.096,30870
```

Times depend on the computer and Java runtime. The program checks that both algorithms return the same number of matches.

The handwritten one-page explanation must be written independently by the student, as required by the assignment.
