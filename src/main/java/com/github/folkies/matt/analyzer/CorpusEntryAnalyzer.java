package com.github.folkies.matt.analyzer;

import java.sql.Connection;

public class CorpusEntryAnalyzer {

	private static String lastUniqueId = "";

	public static String analyzeEntry(CorpusEntry tune) {
        String notation = tune.getNotation();
        notation = MattABCTools.fixNotationForTunepal(notation);

        int tuneStart = MattABCTools.skipHeaders(notation);
        String key = notation.substring(tuneStart);
        String head = notation.substring(0, tuneStart);
        // Disable separate keys for variations
        int iVariation = -1; // key.toUpperCase().indexOf("\"V");
        int start = 0;

        // The comment is at the start, so skip it
        if (iVariation == 0)
        {
            iVariation = key.indexOf("\"", iVariation + 1);
            key = key.substring(iVariation + 1);
            iVariation = key.indexOf("\"");
        }
        if (iVariation!= -1)
        {
            boolean endOfTune = false;
            String body = "";
            while (! endOfTune)
            {
                String subKey = key.substring(start, iVariation);
                body = createCorpusEntry(notation, 1, head, subKey, "Rover", tune.getTitle(), tune.getX(), tune, null);
                // Find the end of the comment
                iVariation = key.indexOf("\"", iVariation + 1);
                start = iVariation + 1;
                // Now find the next variation
                iVariation = key.indexOf("\"", start);
                if (iVariation == -1)
                {
                    endOfTune = true;
                    subKey = key.substring(start, key.length());
                    body = createCorpusEntry(notation, 1, head, subKey, "Rover", tune.getTitle(), tune.getX(), tune, null);
                }
            }
            return body;
        }
        else
        {
            // Create an entry for the whole tune
            return createCorpusEntry(notation, 1, head, key, "Rover", tune.getTitle(), tune.getX(), tune, null);
        }
    }


    private static String createCorpusEntry(String notation, int source, String head, String body, String fileName, String title, int x, CorpusEntry ce, Connection conn)
    {
        String parsons = null;
        String uniqueId = createUniqueTunePalID(x, source, fileName, title);
       try
        {
            body = MattABCTools.stripComments(body);
            body = MattABCTools.stripWords(body);
            body = MattABCTools.stripAdvancedABC(body);
            body = MattABCTools.stripWhiteSpace(body);
            body = MattABCTools.expandLongNotes(body);
            body = MattABCTools.expandParts(body);
            body = MattABCTools.stripBarDivisions(body);
            body = MattABCTools.removeTripletMarks(body);
            body = MattABCTools.removeExtraNotation(body);
            body = body.toUpperCase();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            body = "";
        }
        if (body.length() == 0)
        {
            System.out.println("Could not index: " + title);
        }
        else
        {
            ce.setFile(fileName);
            ce.setSource(source);
            ce.setTitle(MattABCTools.removeBBBitsFromTitle(title));
            ce.setAltTitle(MattABCTools.removeBBBitsFromTitle(ce.getAltTitle()));
            ce.setX(x);
            ce.setKey(body);
            ce.setParsons(parsons);
            if ((!lastUniqueId.equals(uniqueId)))
            {
            	notation = MattABCTools.removeMidiBits(notation);
            	notation = MattABCTools.removeBBBits(notation);
                lastUniqueId = uniqueId;
            }
        }
        return body;
    }

	static String createUniqueTunePalID(int x, int corpus, String fileName, String title)
    {
        String tunePalId = "";
        title = title.replace(System.getProperty("file.separator").charAt(0), '~');
        title = title.replace('\'', '~');
        title = title.replace('?', '~');
        title = title.replace('"', '~');
        title = title.replace(' ', '~');
        tunePalId = "" + x + "-" + fileName + "-" + corpus + "-" + title;
        return tunePalId;
    }
}
