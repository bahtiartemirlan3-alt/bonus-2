import java.util.*;

public class Main {
    static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node fail;
        List<Integer> output = new ArrayList<>();
    }

    static class AhoCorasick {
        Node root = new Node();

        void add(String pattern, int id) {
            Node current = root;
            for (char c : pattern.toCharArray()) {
                current = current.next.computeIfAbsent(c, k -> new Node());
            }
            current.output.add(id);
        }

        void build() {
            Queue<Node> queue = new ArrayDeque<>();
            root.fail = root;
            for (Node child : root.next.values()) {
                child.fail = root;
                queue.add(child);
            }
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                for (Map.Entry<Character, Node> entry : current.next.entrySet()) {
                    char c = entry.getKey();
                    Node child = entry.getValue();
                    Node f = current.fail;
                    while (f != root && !f.next.containsKey(c)) {
                        f = f.fail;
                    }
                    if (f.next.containsKey(c)) {
                        child.fail = f.next.get(c);
                    } else {
                        child.fail = root;
                    }
                    child.output.addAll(child.fail.output);
                    queue.add(child);
                }
            }
        }

        long search(String text) {
            Node current = root;
            long matches = 0;
            for (char c : text.toCharArray()) {
                while (current != root && !current.next.containsKey(c)) {
                    current = current.fail;
                }
                if (current.next.containsKey(c)) {
                    current = current.next.get(c);
                }
                matches += current.output.size();
            }
            return matches;
        }
    }

    static long kmp(String text, String pattern) {
        int[] lps = new int[pattern.length()];
        for (int i = 1, j = 0; i < pattern.length();) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                lps[i++] = ++j;
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        long matches = 0;
        for (int i = 0, j = 0; i < text.length();) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches++;
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    static String randomString(Random random, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) ('a' + random.nextInt(4)));
        }
        return s.toString();
    }

    public static void main(String[] args) {
        Random random = new Random(42);
        int[] counts = {10, 100, 1000};
        int[] lengths = {1000, 10000, 100000};
        System.out.println("Patterns,Text length,Aho avg ms,KMP avg ms,Matches");

        for (int count : counts) {
            List<String> patterns = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                patterns.add(randomString(random, 5 + random.nextInt(4)));
            }
            for (int length : lengths) {
                String text = randomString(random, length);
                double ahoTotal = 0;
                double kmpTotal = 0;
                long matches = 0;

                for (int run = 0; run < 5; run++) {
                    long start = System.nanoTime();
                    AhoCorasick aho = new AhoCorasick();
                    for (int i = 0; i < patterns.size(); i++) {
                        aho.add(patterns.get(i), i);
                    }
                    aho.build();
                    matches = aho.search(text);
                    ahoTotal += (System.nanoTime() - start) / 1_000_000.0;

                    start = System.nanoTime();
                    long kmpMatches = 0;
                    for (String pattern : patterns) {
                        kmpMatches += kmp(text, pattern);
                    }
                    kmpTotal += (System.nanoTime() - start) / 1_000_000.0;
                    if (matches != kmpMatches) {
                        throw new RuntimeException("Different match counts");
                    }
                }
                System.out.printf(Locale.US, "%d,%d,%.3f,%.3f,%d%n",
                        count, length, ahoTotal / 5, kmpTotal / 5, matches);
            }
        }
    }
}
