package com.github.folkies.matt.analyzer;

import org.junit.jupiter.api.Test;

public class MattABCToolsTest {


	@Test
	public void shouldExpandParts() {
		String notes = "DDDF|:AAAGAA|1DADDDF:|2DDDDDD";
		String expanded = MattABCTools.expandParts(notes);
		System.out.println(expanded);
	}

	@Test
	public void shouldExpandPart2() {
		String notes = "|:ddAFAD|1)FAdfge:||2)FADDDD|";
		String expanded = MattABCTools.expandParts(notes);
		System.out.println(expanded);
	}


}
