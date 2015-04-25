package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.Character;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
import org.junit.Test;

public class PlusTest {
	
	@Test
	public void testAutomaton() {
		RegularExpression regex = Plus.from(Character.from('a'));
		assertEquals(5, regex.getAutomaton().getCountStates());
		
		Matcher matcher = MatcherFactory.getMatcher(regex);
		
		assertEquals(1, matcher.match(Input.fromString("a"), 0));
		assertEquals(2, matcher.match(Input.fromString("aa"), 0));
		assertEquals(3, matcher.match(Input.fromString("aaa"), 0));
		assertEquals(6, matcher.match(Input.fromString("aaaaaa"), 0));
		assertEquals(17, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaa"), 0));
		assertEquals(33, matcher.match(Input.fromString("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"), 0));
		
		assertFalse(matcher.match(Input.fromString("")));
	}

}