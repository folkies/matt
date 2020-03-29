/*
 * ABCTools.java
 *
 * Created on 17 July 2007, 16:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.github.folkies.matt.analyzer;

/**
 *
 * @author Bryan
 */
public class MattABCTools {

	static String fixNotationForTunepal(String notation) {
		// Fix up CR's
		if (notation.indexOf("I:linebreak $") != -1)
		{
			int tuneStart = skipHeaders(notation);
			String justTune = notation.substring(tuneStart);
			justTune = justTune.replace("\r", "");
			justTune = justTune.replace("\r\n", "");
			justTune = justTune.replace("\n", "");
			justTune = justTune.replace("$ ", "$");
			justTune = justTune.replace("$", "\n");
			justTune = justTune.replaceAll("[^\\n]w:", "\nw:");

			notation = notation.substring(0, tuneStart) + justTune;
		}
		return notation;
	}

    static String removeExtraNotation(String key) {
        String ret = key.replaceAll(">", "");

        ret = ret.replaceAll("<", "");
        ret = ret.replaceAll("/", "");
        ret = ret.replace("\\", "");
        ret = ret.replace("(", "");
        ret = ret.replace(")", "");
        ret = ret.replace("/", "");
        ret = ret.replace("-", "");
        ret = ret.replace("!", "");
        ret = ret.replace("_", "");

        // remove guitar chords
        ret = ret.replaceAll("\\[.\\]", "");
        ret = ret.replaceAll("\\[..\\]", "");
        ret = ret.replaceAll("\\[...\\]", "");
        ret = ret.replaceAll("\\[....\\]", "");
        ret = ret.replaceAll("\\[.....\\]", "");
        ret = ret.replaceAll("\\[.*\\]", "");


        StringBuffer ret1 = new StringBuffer();
        for (int i = 0 ; i < ret.length() ; i ++)
        {
            char cur = ret.charAt(i);
            if (((cur >= 'A') && (cur <= 'G')) || ((cur >= 'a') && (cur <= 'g')))
            {
                ret1.append(cur);
            }

        }
        ret = ret1.toString();

        return ret;
    }

    static String removeLongNotes(String key) {
        StringBuffer ret = new StringBuffer();
        char lastChar = '*';

        for (int i = 0 ; i < key.length() ; i ++)
        {
            char current = key.charAt(i);
            if (current != lastChar)
            {
                ret.append(current);
                lastChar = current;
            }
        }

        return ret.toString();
    }

    enum partTypes {NORMAL, REPEAT, SPECIAL12};

    /** Creates a new instance of ABCTools */
    public MattABCTools() {
    }

    public static int skipHeaders(String tune)
    {
        int i = 0;
        int inChars = 0;
        boolean inHeader = true;

        while ((i < tune.length()) && (inHeader))
        {
            char c = tune.charAt(i);
            if (inChars == 1)
            {
                if (((c == ':') && (tune.charAt(i-1) != '|')) || ((c == '%') && (tune.charAt(i-1) == '%')))
                {
                    inHeader = true;
                }
                else
                {
                    inHeader = false;
                    i -=2;
                }
            }
            if ((c == '\r') || (c == '\n'))
            {
                inChars = -1;
            }
            i ++;
            inChars ++;
        }
        return i;
    }

    public static String expandParts(String notes)
    {
    	StringBuffer retValue = new StringBuffer(notes);
    	try
    	{

	        int start = 0;
	        int end = 0;
	        String endToken = ":|";
	        int count = 0;
	        while (true)
	        {
	        	if (count > 30)
	        	{
	        		throw new ArrayIndexOutOfBoundsException("Too many parts in tune: " + notes);
	        	}
	        	count ++;
	            end = retValue.indexOf(endToken);

	            if ((end == -1))
	            {
	                break;
	            }
	            else
	            {
	                int newStart = retValue.lastIndexOf("|:", end - 2);
	                if (newStart != -1)
	                {
	                    start = newStart + 2;
	                }
	                if ((retValue.length() > end + 2) && Character.isDigit(retValue.charAt(end + 2)))
	                {
	                    int numSpecialBars = 1;
	                    StringBuffer expanded = new StringBuffer();
	                    int normalPart = retValue.lastIndexOf("|", end);
	                    if (! Character.isDigit(retValue.charAt(normalPart + 1)))
	                    {
	                        normalPart = retValue.lastIndexOf("|", normalPart - 1);
	                        numSpecialBars ++;
	                    }
	                    expanded.append(retValue.substring(start, normalPart));
	                    expanded.append("|");
	                    expanded.append(retValue.substring(normalPart + 2, end));
	                    int secondTime = end;
	                    while ((numSpecialBars --) > 0)
	                    {
	                        secondTime = retValue.indexOf("|", secondTime + 2);
	                    }
	                    if (secondTime == -1) {
	                    	secondTime = retValue.length();
	                    }
	                    expanded.append("|");
	                    expanded.append(retValue.substring(start, normalPart));
	                    expanded.append("|");
	                    expanded.append(retValue.substring(end + 3, secondTime));
	                    expanded.append("|");
	                    retValue.replace(start, secondTime, expanded.toString());
	                }
	                else
	                {
	                    StringBuffer expanded = new StringBuffer();
	                    String part = stripTrailing(retValue.substring(start, end), ':');
						expanded.append(part);
	                    expanded.append("|");
	                    expanded.append(part);
	                    retValue.replace(start, end + 2, expanded.toString());
	                    start = start + expanded.toString().length();
	                }
	            }
	        }
    	}
    	catch (Exception e)
    	{
    		System.out.println(notes);
    		e.printStackTrace();
    		retValue = new StringBuffer(notes);
    	}
        return retValue.toString();
    }

    public static String expandRepeats(String notes) {
    	StringBuilder expanded = new StringBuilder();
    	int cursor = 0;
    	int end = notes.indexOf(":|");
    	while (end != -1) {
            int start = notes.lastIndexOf("|:", end - 2);
            if (cursor < start) {
            	expanded.append(notes.substring(cursor, start));
            	expanded.append("|");
            	cursor = start + 2;
            }

            if (start < cursor) {
                start = cursor;
            } else {
            	start += 2;
            }

            int nextCursor = end + 2;
            int secondVariant = indexOfSecondVariant(notes, nextCursor);

			if (secondVariant == -1) {
				String segment = stripTrailing(notes.substring(start, end), ':');
				expanded.append(segment);
				expanded.append("|");
				expanded.append(segment);
				expanded.append("|");
				cursor = end + 2;
			} else {
				int firstVariant = indexOfFirstVariant(notes, cursor, end);
				String commonSegment = notes.substring(start, firstVariant - 2);
				expanded.append(commonSegment);
				String firstEnding = notes.substring(firstVariant, end);
				expanded.append("|");
				expanded.append(firstEnding);
				expanded.append("|");
				expanded.append(commonSegment);
				expanded.append("|");
				cursor = secondVariant;
			}
            end = notes.indexOf(":|", cursor);

    	}
    	if (cursor < notes.length() - 1) {
    		expanded.append(notes.substring(cursor));
    	}
    	return expanded.toString();
    }


    private static int indexOfFirstVariant(String notes, int from, int to) {
        int index = notes.indexOf("|1", from);
        if (index != -1 && index < to) {
        	return index + 2;
        }
        index = notes.indexOf("[1", from);
        if (index != -1 && index < to) {
        	return index + 2;
        }
		return -1;
	}

	private static int indexOfSecondVariant(String notes, int from) {
		int pos = from;
		while (pos < notes.length()) {
			if (Character.isWhitespace(notes.charAt(pos))) {
				pos++;
			} else {
				break;
			}
		}
		if (pos == notes.length()) {
			return -1;
		}
		if (notes.charAt(pos) == '[') {
			pos++;
		}
		if (pos == notes.length()) {
			return -1;
		}
		if (Character.isDigit(notes.charAt(pos))) {
			return pos + 1;
		}
		return -1;
	}

	private static String stripTrailing(String notes, char c) {
    	int pos = notes.length() -1;
    	while (pos >= 0) {
    		if (notes.charAt(pos) != c) {
    			break;
    		}
    		pos--;
    	}
		return notes.substring(0, pos + 1);
	}

	public static String stripBarDivisions(String notes)
    {
        StringBuffer retValue = new StringBuffer();

        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if ((c != '|') && (c != ':'))
            {
                retValue.append(c);
            }
        }
        return retValue.toString();
    }

    public static String removeTripletMarks(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if ((c == '(') && Character.isDigit(notes.charAt(i+1)))
            {
                i +=1;
                continue;
            }
            retValue.append(c);
        }
        return retValue.toString();
    }

    public static String expandLongNotes(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        // First remove ornaments
        boolean inOrnament = false;
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);
            if (c == '{')
            {
                inOrnament = true;
                continue;
            }
            if (c == '}')
            {
                inOrnament = false;
                continue;
            }

            if ((c != '~') && ! inOrnament && (c != ',') && (c != '=') && (c != '^') && (c != '\''))
            {
                retValue.append(c);
            }
        }
        for (int i = 1 ;  i < retValue.length(); i ++)
        {
            char c  = retValue.charAt(i);
            char p = retValue.charAt(i -1);
            // Its a long note
            if (Character.isDigit(c) && Character.isLetter(p))
            {
                String expanded = "";
                int howMany = c - '0';
                for (int j = 0 ; j < howMany; j ++)
                {
                    expanded += p;
                }
                retValue.replace(i - 1, i + 1, expanded);
            }
            // Expand Z's
            /*
             if (c == 'z')
            {
                retValue.replace(i, i, "" + p);
            }
             */
        }
        return retValue.toString();
    }

    public static String stripNonNotes(String notes)
    {
        StringBuffer retValue = new StringBuffer();
        notes = stripComments(notes);
        for (int i = 0 ;  i < notes.length(); i ++)
        {
            char c  = notes.charAt(i);

            if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')) || ((c >= '1') && (c <= '9')) || (c == '('))
            {
                retValue.append(c);
            }
        }
        return retValue.toString();
    }

    public static String stripWhiteSpace(String transcription)
    {
        StringBuffer retValue = new StringBuffer();
        int i = 0;
        while (i < transcription.length())
        {
            if ((transcription.charAt(i) != ' ') &&  (transcription.charAt(i) != '\r') && (transcription.charAt(i) != '\n'))
            {
                retValue.append(transcription.charAt(i));
            }
            i ++;
        }

        return retValue.toString();
    }

    public static String stripComments(String transcription)
    {
        StringBuffer retValue = new StringBuffer();

        int i = 0;
        boolean inComment = false;
        while (i < transcription.length())
        {

            if (transcription.charAt(i) == '"')
            {
                if (inComment)
                {
                    inComment = false;
                    i ++;
                    continue;
                }
                else
                {
                    inComment = true;
                }
            }
            if (!inComment)
            {
                retValue.append(transcription.charAt(i));
            }
            i ++;
        }
        return retValue.toString();
    }

    public static String stripAll(String key)
    {
        key = MattABCTools.stripComments(key);
        key = MattABCTools.stripWhiteSpace(key);
        key = MattABCTools.expandLongNotes(key);
        key = MattABCTools.expandParts(key);
        key = MattABCTools.stripBarDivisions(key);
        key = MattABCTools.removeTripletMarks(key);
        key = MattABCTools.removeExtraNotation(key);
        return key.toUpperCase();
    }

    public static String stripAdvancedABC(String body)
    {
    	String ret = body;
    	ret = ret.replace("!fermata!", "");
    	ret = ret.replace("!trill)!", "");
    	ret = ret.replace("!trill(!", "");
    	ret = ret.replace("!turn!", "");
    	return ret;
    }

	public static String stripWords(String body) {
		String ret = "";
		String lines[] = body.split("\\r?\\n");
		for(String line: lines)
		{
			if (line.startsWith("W:") || line.startsWith("w:"))
			{
				// Logger.log("Removing: " + line);
			}
			else
			{
				ret += line + "\n";
			}
		}

		return ret;
	}

	public static String removeMidiBits(String notation)
	{
    	StringBuffer ret = new StringBuffer();
    	if ((notation.indexOf("Q:") == -1 ) && (notation.indexOf("q:") == -1))
    	{
    		return notation;
    	}
    	else
    	{
    		String lines[] = notation.split("\\r?\\n");
    		for(String line:lines)
    		{
    			if (!line.toLowerCase().startsWith("%%midi program"))
    			{
    				ret.append(line + "\n");
    			}
    		}
    		return ret.toString();
    	}
	}

	public static String removeBBBits(String notation) {
		StringBuffer ret = new StringBuffer();


		if (notation != null)
		{
			String lines[] = notation.split("\\r?\\n");

			for(String line:lines)
			{
				if (line.toLowerCase().startsWith("t:"))
				{
					int i = line.lastIndexOf("%");
					if (i != -1)
					{
						line = line.substring(0, i).trim();
					}
				}
				ret.append(line + "\n");
			}
		}
		return ret.toString();
	}

	public static String removeBBBitsFromTitle(String title)
	{
		if (title != null)
		{
			int i = title.lastIndexOf("%");
			if (i != -1)
			{
				title = title.substring(0, i).trim();
			}
		}
		return title;
	}
}
