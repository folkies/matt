package com.github.folkies.matt.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

@ApplicationScoped
public class TuneMatcher {

	private static final int MAX_KEY_LENGTH = 1000;
	private static final int NGRAM_LENGTH = 4;
	private static final int MAX_CANDIDATES = 1000;
	private static final int MAX_RESULTS = 10;

	@Inject
	Corpus corpus;

	public List<MatchedTune> findBestMatches(String query) {
		return findBestMatchesTwoPass(query);
	}

	/**
	 * Finds best matches by computing edit distance for all tune settings from the
	 * corpus.
	 * 
	 * @param query query string
	 * @return best matches sorted by edit distance.
	 */
	public List<MatchedTune> findBestMatchesOnePass(String query) {
		if (query == null || query.length() < NGRAM_LENGTH) {
			return Collections.emptyList();
		}
		List<MatchedTune> results = new ArrayList<>();
		PriorityBlockingQueue<MatchedTune> queue = new PriorityBlockingQueue<>();

		corpus.getTunes().parallelStream().forEach(tune -> {
			int[][] d = new int[2][MAX_KEY_LENGTH + 1];
			String normalized = tune.getNormalized();
			if (normalized.length() > MAX_KEY_LENGTH) {
				normalized = normalized.substring(0, MAX_KEY_LENGTH);
			}

			int ed = EditDistance.minEditDistance(query, normalized, d);
			MatchedTune matchedTune = createMatchedTune(tune, ed, query.length());
			queue.add(matchedTune);
		});

		String tuneId = "";
		while (results.size() < MAX_RESULTS) {
			MatchedTune tune = queue.poll();
            if (!tune.getTune().equals(tuneId)) {
                tuneId = tune.getTune();
                results.add(tune);
            }
		}
		return results;
	}

	/**
	 * Finds best matches in two passes, first selecting a number of likely
	 * candidates by n-gram matching, and then computing the edit distance
	 * for the candidates only.
	 * 
	 * @param query query string
	 * @return best matches sorted by edit distance.
	 */
	public List<MatchedTune> findBestMatchesTwoPass(String query) {
		if (query == null || query.length() < NGRAM_LENGTH) {
			return Collections.emptyList();
		}
		Set<String> ngrams = new HashSet<>();
		for (int i = 0; i < query.length() - NGRAM_LENGTH; i++) {
			ngrams.add(query.substring(i, i + NGRAM_LENGTH));
		}
		Trie trie = Trie.builder().addKeywords(ngrams).build();
		long numNgrams = ngrams.size();

		PriorityBlockingQueue<MatchedTune> queue1 = new PriorityBlockingQueue<>();

		corpus.getTunes().parallelStream().forEach(tune -> {
			String normalized = tune.getNormalized();
			if (normalized.length() > MAX_KEY_LENGTH) {
				normalized = normalized.substring(0, MAX_KEY_LENGTH);
			}

			Collection<Emit> emits = trie.parseText(normalized);
			long matchingNgrams = emits.stream().map(Emit::getKeyword).distinct().count();

			int ed = (int)(numNgrams - matchingNgrams);
			MatchedTune matchedTune = createMatchedTune(tune, ed, query.length());
			queue1.add(matchedTune);
		});

		List<MatchedTune> candidates = new ArrayList<>();
		queue1.drainTo(candidates, MAX_CANDIDATES);


		PriorityBlockingQueue<MatchedTune> queue2 = new PriorityBlockingQueue<>();
		candidates.parallelStream().forEach(candidate -> {
			int[][] d = new int[2][MAX_KEY_LENGTH + 1];
			String normalized = candidate.getNormalized();
			if (normalized.length() > MAX_KEY_LENGTH) {
				normalized = normalized.substring(0, MAX_KEY_LENGTH);
			}

			int ed = EditDistance.minEditDistance(query, normalized, d);
			MatchedTune matchedTune = createMatchedTune(candidate, ed, query.length());
			queue2.add(matchedTune);
		});

		List<MatchedTune> results = new ArrayList<>();
		String tuneId = "";
		while (results.size() < MAX_RESULTS) {
			MatchedTune tune = queue2.poll();
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
