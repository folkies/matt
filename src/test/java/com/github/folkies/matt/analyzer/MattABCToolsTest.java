package com.github.folkies.matt.analyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MattABCToolsTest {

	static Object[][] expandParts() {
		return new Object[][] {
			{ "ABC|DEF:|", "ABC|DEF|ABC|DEF"},
			{ "|ABC|DEF:|", "|ABC|DEF||ABC|DEF"},
			{ "|:ABC|DEF:|", "|:ABC|DEF|ABC|DEF"},
			{ "ABC|1DEF:|2AAA|", "ABC|DEF|ABC|AAA||"},
			{ "ABC|1 DEF:|2 AAA|", "ABC| DEF|ABC| AAA||"},
			{ "GGG|:ABC|DEF:|AAA|:ABC|CBA:|", "GGG|:ABC|DEF|ABC|DEFAAA|:ABC|CBA|ABC|CBA"},
			{ "ABC|DEF:|GGG|BBB:|", "ABC|DEF|ABC|DEFGGG|BBB|GGG|BBB"},

			{ "DDDF|:AAAGAA|1DADDDF:|2DDDDDD", "DDDF|:AAAGAA|DADDDF|AAAGAA|DDDDDD|"},
			{ "|:ddAFAD|1FAdfge:|2FADDDD|]", "|:ddAFAD|FAdfge|ddAFAD|FADDDD||]"},
			{ "DD|GGGABBAG|DDGGGG::|", "DD|GGGABBAG|DDGGGG|DD|GGGABBAG|DDGGGG"},
		};
	}


	static Object[][] expandRepeats() {
		return new Object[][] {
			{ "ABC|DEF:|", "ABC|DEF|ABC|DEF|"},
			{ "ABC|DEF:|GAB", "ABC|DEF|ABC|DEF|GAB"},
			{ "|ABC|DEF:|", "|ABC|DEF||ABC|DEF|"},
			{ "|:ABC|DEF:|", "ABC|DEF|ABC|DEF|"},
			{ "GGG|:ABC|DEF:|AAA|:ABC|CBA:|", "GGG|ABC|DEF|ABC|DEF|AAA|ABC|CBA|ABC|CBA|"},
			{ "ABC|DEF:|GGG|BBB:|", "ABC|DEF|ABC|DEF|GGG|BBB|GGG|BBB|"},
			{ "DD|GGGABBAG|DDGGGG::|", "DD|GGGABBAG|DDGGGG|DD|GGGABBAG|DDGGGG|"},

			{ "DDDF|:AAAGAA|1DADDDF:|2DDDDDD", "DDDF|AAAGAA|DADDDF|AAAGAA|DDDDDD"},
			{ "|:ddAFAD|1FAdfge:|2FADDDD|]", "ddAFAD|FAdfge|ddAFAD|FADDDD|]"},
			{ "ABC|1DEF:|2AAA|", "ABC|DEF|ABC|AAA|"},
			{ "ABC|1 DEF:|2 AAA|", "ABC| DEF|ABC| AAA|"},
		};
	}


	@ParameterizedTest
	@MethodSource("expandParts")
	public void shouldExpandParts(String notes, String expanded) {
		assertThat(MattABCTools.expandParts(notes)).isEqualTo(expanded);
	}

	@ParameterizedTest
	@MethodSource("expandRepeats")
	public void shouldExpandRepeats(String notes, String expanded) {
		assertThat(MattABCTools.expandRepeats(notes)).isEqualTo(expanded);
	}

	@Test
	public void shouldExpandParts2() {
		String notes = "|:ddAFAD|1)FAdfge:||2)FADDDD|";
		String expanded = MattABCTools.expandParts(notes);
		System.out.println(expanded);
	}

	@Test
	public void shouldExpandParts2a() {
		String notes = "|:ddAFAD|1FAdfge:|2FADDDD|]";
		String expanded = MattABCTools.expandParts(notes);
		assertThat(expanded).isEqualTo("|:ddAFAD|FAdfge|ddAFAD|FADDDD||]");
	}

	@Test
	public void shouldExpandParts3() {
		String notes = "DD|GGGABBAG|DDGGGG::|";
		String expanded = MattABCTools.expandParts(notes);
		assertThat(expanded).isEqualTo("DD|GGGABBAG|DDGGGG|DD|GGGABBAG|DDGGGG");
	}

	@Test
	public void shouldExpandParts4() {
		String notes = "|:bagffdBBdee[1B:|2z|]";
		String expanded = MattABCTools.expandParts(notes);
		System.out.println(expanded);
	}


}
