package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= B A + A | a
 * 
 * B ::= b | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */

public class HiddenLeftRecursion {
	
	private Grammar grammar;

	@Before
	public void createGrammar() {
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");

		Rule r1 = new Rule(A, list(B, A, new Character('+'), A));
		Rule r2 = new Rule(A, list(new Character('a')));
		
		Rule r3 = new Rule(B, list(new Character('b')));
		Rule r4 = new Rule(B);
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("IndirectRecursion", factory).addRule(r1)
													  .addRule(r2)
													  .addRule(r3)
													  .addRule(r4);
		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("ba+a+a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
	}

}