package com.github.folkies.matt.server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TuneMatcher {

	private static final int MAX_KEY_LENGTH = 1000;

	@Inject
	Corpus corpus;

	public List<MatchedTune> findBestMatches(String query) {
		List<MatchedTune> results = new ArrayList<>();
		PriorityBlockingQueue<MatchedTune> queue = new PriorityBlockingQueue<>();

		corpus.getTunes().parallelStream().forEach(tune -> {
			int[][] d = new int[2][MAX_KEY_LENGTH + 1];
			String normalized = tune.getNormalized();
			if (normalized.length() > 1000) {
				normalized = normalized.substring(0, 1000);
			}

			int ed = EditDistance.minEditDistance(query, normalized, d);
			MatchedTune matchedTune = createMatchedTune(tune, ed, query.length());
			queue.add(matchedTune);
		});

		String tuneId = "";
		while (results.size() < 10) {
			MatchedTune tune = queue.poll();
            if (!tune.getTune().equals(tuneId)) {
                tuneId = tune.getTune();
                results.add(tune);
            }
		}
		return results;
	}


	private MatchedTune createMatchedTune(NormalizedTune tune, int ed, int length) {
		MatchedTune matchedTune = new MatchedTune();
		matchedTune.setKey(tune.getKey());
		matchedTune.setName(tune.getName());
		matchedTune.setNormalized(tune.getNormalized());
		matchedTune.setRhythm(tune.getRhythm());
		matchedTune.setSetting(tune.getSetting());
		matchedTune.setTune(tune.getTune());
		matchedTune.setEd(ed);
		matchedTune.setConfidence(1.0f - ((float) ed / length));
		return matchedTune;
	}
}
