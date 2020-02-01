package com.github.folkies.matt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;


public class TheSessionParser {

	public void parseTunes(InputStream is) throws IOException {
		JsonArrayBuilder normalizedTunes = Json.createArrayBuilder();
		int numPrinted = 0;
		JsonReader reader = Json.createReader(is);
		JsonArray tunes = reader.readArray();
		for (JsonObject tune : tunes.getValuesAs(JsonObject.class)) {
			String notation = toTuneBook(tune);
			CorpusEntry entry = new CorpusEntry();
			entry.setNotation(notation);
			entry.updateFromNotation();
			String body = CorpusEntryAnalyzer.analyzeEntry(entry);
			if (!body.isEmpty()) {
				System.out.println(String.format("%d\t%s", entry.getX(), body));
				numPrinted++;
				normalizedTunes.add(Json.createObjectBuilder()
							.add("tune", tune.getString("tune"))
							.add("setting", tune.getString("setting"))
							.add("name", tune.getString("name"))
							.add("rhythm", tune.getString("type"))
							.add("key", transformKey(tune.getString("mode")))
							.add("normalized", body));
			}
		}
		System.out.println("Total  : " + tunes.size());
		System.out.println("Printed: " + numPrinted);
		OutputStream os = new FileOutputStream("index.json");
		JsonWriter writer = Json.createWriter(os);
		writer.writeArray(normalizedTunes.build());
		writer.close();
	}


	private String toTuneBook(JsonObject tune) {
		String settingId = tune.getString("setting");
		String title = tune.getString("name");
		String rhythm = tune.getString("type");
		String meter = tune.getString("meter");
		String key = tune.getString("mode");
		String notation = tune.getString("abc");

		StringBuilder sb = new StringBuilder();
		sb.append("X: ");
		sb.append(settingId);
		sb.append("\n");

		sb.append("T: ");
		sb.append(title);
		sb.append("\n");

		sb.append("M: ");
		sb.append(meter);
		sb.append("\n");

		sb.append("L: ");
		sb.append("1/8");
		sb.append("\n");

		sb.append("R: ");
		sb.append(rhythm);
		sb.append("\n");

		sb.append("K: ");
		sb.append(key);
		sb.append("\n\n");
		sb.append(notation);
		return sb.toString();
	}

	private String transformKey(String key) {
		String fundamental = key.substring(0, 1);
		String mode = key.substring(1);
		if (mode.equals("major")) {
			return fundamental;
		}
		if (mode.equals("minor")) {
			return fundamental + "min";
		}
		return key.substring(0, 4);
	}


	public void parseTunesFromLocalFile() throws IOException {
		InputStream is = new FileInputStream("/Users/hwellmann/Downloads/tunes.json");
		parseTunes(is);
	}

	public void parseTunesFromGitHub() throws IOException {
		URL url = new URL("https://raw.githubusercontent.com/adactio/TheSession-data/master/json/tunes.json");
		InputStream is = url.openStream();
		parseTunes(is);
	}

	public static void main(String[] args) throws IOException {
		TheSessionParser parser = new TheSessionParser();
		parser.parseTunesFromGitHub();
	}
}
