package com.github.folkies.matt.server;

/**
 * Computes the mininum <a href=
 * "https://en.wikipedia.org/wiki/Approximate_string_matching#Problem_formulation_and_algorithms">edit
 * distance</a>
 * between {@code pattern} and any substring of {@code text}.
 * <p>
 * Let d(i, j) be the minimum edit distance between the prefix of length i of
 * the pattern and all substrings of the text ending at position j-1
 * where i = 0, ..., pattern.length() and j = 0, ..., text.length(). For j = 0,
 * the text substring is the empty string.
 * <p>
 * For p = pattern.length(), the mininum of d(p, j) over all j is the desired
 * result.
 * <p>
 * The matrix can be computed row by row:
 * <p>
 * d(0, j) = 0 for all j.
 * <p>
 * d(i, 0) = i for all i.
 * <p>
 * d(i, j) = min( d(i-1, j-1) + diff, d(i-1, j) + 1, d(i, j-1) + 1), where diff
 * = 0 if pattern[i-1] == text[j-1] and diff = 1 otherwise.
 * <p>
 * To save space, we do not store the entire matrix d(i,j). We use to arrays
 * prev and curr to represent rows i-1 and i.
 */
public class EditDistance {

    public static int[] editDistance(String pattern, String text, int[] prev, int[] curr) {
        int pLength = pattern.length();
        int tLength = text.length();
        int difference = 0;

        if (pLength == 0) {
            return null;
        }
        if (tLength == 0) {
            return null;
        }

        for (int i = 0; i < tLength + 1; i++) {
            prev[i] = 0;
        }

        for (int i = 1; i <= pLength; i++) {
            curr[0] = i;
            char sc = pattern.charAt(i - 1);
            for (int j = 1; j <= tLength; j++) {
                int v = prev[j - 1];
                if ((text.charAt(j - 1) != sc)) {
                    difference = 1;
                } else {
                    difference = 0;
                }
                int a = prev[j] + 1;
                int b = curr[j - 1] + 1;
                int c = v + difference;
                int min = (a < b) ? a : b;
                if (c < min) {
                    min = c;
                }
                curr[j] = min;
            }
            int[] tmp = curr;
            curr = prev;
            prev = tmp;
        }
        return prev;
    }

    public static int minEditDistance(String pattern, String text, int[][] d) {
        int[] lastRow = editDistance(pattern, text, d[0], d[1]);
        int min = Integer.MAX_VALUE;
        int tLength = text.length();
        for (int i = 0; i < tLength + 1; i++) {
            int c = lastRow[i];
            if (c < min) {
                min = c;
            }
        }
        return min;
    }
}
