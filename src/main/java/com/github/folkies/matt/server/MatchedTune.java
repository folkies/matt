package com.github.folkies.matt.server;

public class MatchedTune extends NormalizedTune  implements Comparable<MatchedTune> {
	private int ed;
	private float confidence;


	public int getEd() {
		return ed;
	}


	public void setEd(int ed) {
		this.ed = ed;
	}


	public float getConfidence() {
		return confidence;
	}


	public void setConfidence(float confidence) {
		this.confidence = confidence;
	}


	@Override
	public int compareTo(MatchedTune other) {
		return this.ed - other.ed;
	}

}
