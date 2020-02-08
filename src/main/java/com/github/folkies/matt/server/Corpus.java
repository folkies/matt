package com.github.folkies.matt.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@ApplicationScoped
public class Corpus {

	private List<NormalizedTune> tunes;

	@SuppressWarnings("serial")
	@PostConstruct
	void load() throws IOException {
		InputStream is = new FileInputStream("/Users/hwellmann/git/tunebrowser/src/assets/normalized-tunes.json");
		Jsonb jsonb = JsonbBuilder.create();
		tunes = jsonb.fromJson(is, new ArrayList<NormalizedTune>(){}.getClass().getGenericSuperclass());
	}

	public List<NormalizedTune> getTunes() {
		return Collections.unmodifiableList(tunes);
	}
}
