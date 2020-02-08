package com.github.folkies.matt.server;

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

	    char ZETT = 'Z';
	    for (int i = 1; i <= pLength; i++) {
	        curr[0] = i;
	        char sc = pattern.charAt(i - 1);
	        for (int j = 1; j <= tLength; j++) {
	            int v = prev[j - 1];
	            if ((text.charAt(j - 1) != sc) && sc != ZETT) {
	                difference = 1;
	            }
	            else {
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
