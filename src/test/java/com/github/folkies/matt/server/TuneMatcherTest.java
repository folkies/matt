package com.github.folkies.matt.server;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TuneMatcherTest {

	@Inject
	TuneMatcher matcher;

	@Inject
	Corpus corpus;

    @Test
    public void shouldInjectCorpus() {
    	System.out.println("size = " + corpus.getTunes().size());
    }

	@Test
	public void shouldFindTune() {
		String query = "CEGFGEDDCDDDADDFAAFADFAAAFFGAGDGEDCDEGGFGAFDDFEDDCEBBCCEDCEFGEDCDDDEDFAAFADFAEAFGADGEDD";
		List<MatchedTune> matches = matcher.findBestMatches(query);
		for (MatchedTune tune : matches) {
			System.out.println(tune.getEd() + " " + tune.getSetting() + " " + tune.getName());
			System.out.println(tune.getNormalized());
		}
	}

	@Test
	public void shouldFindTune2() {
		String query = "DEDFDFGGCAGGEEDCAGDADDEFDDDADFFGGFECGCFGEEFGGEFDGDFEEFC";
		List<MatchedTune> matches = matcher.findBestMatches(query);
		for (MatchedTune tune : matches) {
			System.out.println(tune.getEd() + " " + tune.getSetting() + " " + tune.getName());
			System.out.println(tune.getNormalized());
		}
	}

	@Test
	public void shouldFindTune3() {
		String query = "DGEDFCDCAFFDACCAGGGFGAGFGAAGFDCADGGBCDDGFGADEFGAAGFDCCDCAGFGEDDGFGADD";
		List<MatchedTune> matches = matcher.findBestMatches(query);
		for (MatchedTune tune : matches) {
			System.out.println(tune.getEd() + " " + tune.getSetting() + " " + tune.getName());
			System.out.println(tune.getNormalized());
		}
	}
}